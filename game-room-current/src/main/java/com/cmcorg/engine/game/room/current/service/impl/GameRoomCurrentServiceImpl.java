package com.cmcorg.engine.game.room.current.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg.engine.game.auth.model.constant.GameAuthConstant;
import com.cmcorg.engine.game.auth.util.GameAuthUserUtil;
import com.cmcorg.engine.game.room.config.model.entity.GameRoomConfigDO;
import com.cmcorg.engine.game.room.config.model.enums.GameRoomConfigPlayTypeEnum;
import com.cmcorg.engine.game.room.config.service.GameRoomConfigService;
import com.cmcorg.engine.game.room.current.mapper.GameRoomCurrentMapper;
import com.cmcorg.engine.game.room.current.model.dto.GameRoomCurrentJoinRoomDTO;
import com.cmcorg.engine.game.room.current.model.dto.GameRoomCurrentPageDTO;
import com.cmcorg.engine.game.room.current.model.entity.GameRoomCurrentDO;
import com.cmcorg.engine.game.room.current.model.vo.GameRoomCurrentJoinRoomVO;
import com.cmcorg.engine.game.room.current.model.vo.GameRoomCurrentPageVO;
import com.cmcorg.engine.game.room.current.service.GameRoomCurrentService;
import com.cmcorg.engine.game.socket.server.model.entity.GameSocketServerDO;
import com.cmcorg.engine.game.socket.server.model.enums.SocketServerRedisKeyEnum;
import com.cmcorg.engine.game.socket.server.service.GameSocketServerService;
import com.cmcorg.engine.game.user.connect.model.entity.GameUserConnectDO;
import com.cmcorg.engine.game.user.connect.service.GameUserConnectService;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.model.entity.BaseEntity;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.auth.util.AuthUserUtil;
import com.cmcorg.engine.web.datasource.util.TransactionUtil;
import com.cmcorg.engine.web.model.model.constant.BaseConstant;
import com.cmcorg.engine.web.model.model.constant.LogTopicConstant;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;
import com.cmcorg.engine.web.redisson.util.RedissonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = LogTopicConstant.ROOM_CURRENT)
public class GameRoomCurrentServiceImpl extends ServiceImpl<GameRoomCurrentMapper, GameRoomCurrentDO>
    implements GameRoomCurrentService {

    @Resource
    GameSocketServerService gameSocketServerService;
    @Resource
    RedissonClient redissonClient;
    @Resource
    GameRoomConfigService gameRoomConfigService;
    @Resource
    GameUserConnectService gameUserConnectService;

    /**
     * 分页排序查询
     */
    @Override
    public Page<GameRoomCurrentPageVO> myPage(GameRoomCurrentPageDTO dto) {

        Page<GameRoomCurrentPageVO> gameRoomCurrentPageVOPage =
            baseMapper.myPage(dto.getCreateTimeDescDefaultOrderPage(), dto);

        if (CollUtil.isNotEmpty(gameRoomCurrentPageVOPage.getRecords())) {

            Set<Long> roomCurrentIdSet =
                gameRoomCurrentPageVOPage.getRecords().stream().map(GameRoomCurrentPageVO::getId)
                    .collect(Collectors.toSet());

            // 获取：每个：当前房间的连接数
            Map<Long, Long> roomCurrentConnectMap = getRoomCurrentConnectMapByRoomCurrentIdSet(roomCurrentIdSet);

            for (GameRoomCurrentPageVO item : gameRoomCurrentPageVOPage.getRecords()) {
                item.setRoomCurrentConnectTotal(roomCurrentConnectMap.getOrDefault(item.getId(), 0L));
            }

        }

        return gameRoomCurrentPageVOPage;
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public GameRoomCurrentDO infoById(NotNullId notNullId) {
        return getById(notNullId.getId());
    }

    /**
     * 批量删除
     */
    @Override
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        removeByIds(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.OK;
    }

    /**
     * 加入房间
     */
    @Override
    public GameRoomCurrentJoinRoomVO joinRoom(GameRoomCurrentJoinRoomDTO dto) {

        Long currentUserId = AuthUserUtil.getCurrentUserId();

        Long currentGameUserId = GameAuthUserUtil.getCurrentGameUserId();

        // 加锁进行，防止：产生多个连接
        return RedissonUtil
            .doLock(SocketServerRedisKeyEnum.PRE_JOIN_ROOM_GAME_USER_ID.name() + currentGameUserId, () -> {

                // 先执行：重连房间
                GameRoomCurrentJoinRoomVO gameRoomCurrentJoinRoomVO = reconnectRoom(currentUserId, currentGameUserId);

                if (gameRoomCurrentJoinRoomVO != null) {
                    return gameRoomCurrentJoinRoomVO;
                }

                // 携带事务，执行
                return TransactionUtil.transactionExec(() -> {

                    // 获取：socket服务器
                    GameSocketServerDO gameSocketServerDO =
                        getGameSocketServerDO(dto, currentUserId, currentGameUserId);
                    log.info("找到的 socket服务器信息：{}", gameSocketServerDO);

                    // 拿到：返回值
                    return getGameRoomCurrentJoinRoomVO(currentUserId, gameSocketServerDO, currentGameUserId);

                });

            });

    }

    /**
     * 找到：socket服务器
     * 备注：
     * 1. socket服务器，可能没有对应的当前房间，当前房间 可能也没有对应的 socket服务器
     * 2. 房间配置，可能没有对应的当前房间，但是当前房间不能没有不对应的 房间配置
     * 3. 当前房间，可能没有对应的用户连接，但是用户连接不能没有不对应的 当前房间
     */
    @NotNull
    private GameSocketServerDO getGameSocketServerDO(GameRoomCurrentJoinRoomDTO dto, Long currentUserId,
        Long currentGameUserId) {

        // 找到：房间配置
        GameRoomConfigDO gameRoomConfigDO =
            gameRoomConfigService.lambdaQuery().eq(BaseEntity::getId, dto.getRoomConfigId())
                .select(BaseEntity::getId, GameRoomConfigDO::getMaxRoomTotal, GameRoomConfigDO::getMaxUserTotal).one();

        if (gameRoomConfigDO == null) {
            ApiResultVO.error("操作失败：找不到房间配置信息，请联系管理员");
        }

        // 获取：所有的 socket服务器
        List<GameSocketServerDO> gameSocketServerDOList = gameSocketServerService.lambdaQuery()
            .select(GameSocketServerDO::getId, GameSocketServerDO::getHost, GameSocketServerDO::getPort,
                GameSocketServerDO::getMaxConnect).list();

        if (gameSocketServerDOList.size() == 0) {
            ApiResultVO.error("操作失败：找不到 socket服务器，请联系管理员");
        }

        // 上锁：防止重复创建房间
        return RedissonUtil.doLock(SocketServerRedisKeyEnum.PRE_ROOM_CONFIG_ID.name() + dto.getRoomConfigId(), () -> {

            // 获取：所有的 当前房间
            List<GameRoomCurrentDO> gameRoomCurrentDOList = getAllGameRoomCurrentDOList();

            // 组装：连接数，并获取：当前房间配置下的 房间集合
            List<GameRoomCurrentDO> roomCurrentDOList =
                setConnectTotalAndGetConfigRoomCurrentList(gameRoomCurrentDOList, gameSocketServerDOList,
                    gameRoomConfigDO);

            // 采用：每一个房间先把人装满，然后才创建另外一个房间
            if (gameRoomCurrentDOList.size() == 0 || roomCurrentDOList.size() == 0) { // 如果：没有房间，或者 当前房间配置下，没有房间
                if (gameRoomConfigDO.getMaxRoomTotal() != 0) {
                    if (gameRoomConfigDO.getMaxUserTotal() != 0) {
                        log.info("没有房间，或者 当前房间配置下，没有房间，创建新的房间");
                        // 创建所有，通过：连接数最少的 socket服务器
                        return createAllByMinConnectSocketServer(dto, currentUserId, gameSocketServerDOList,
                            currentGameUserId);
                    } else {
                        ApiResultVO.error("操作失败：房间人数已满，请稍后再试");
                    }
                } else {
                    ApiResultVO.error("操作失败：房间数已满，请稍后再试");
                }
            }

            // 找到：人数最少的 房间
            AtomicReference<GameRoomCurrentDO> atomicGameRoomCurrentDO = new AtomicReference<>();

            roomCurrentDOList.stream().min(Comparator.comparing(GameRoomCurrentDO::getCurrentConnectTotal))
                .ifPresent(atomicGameRoomCurrentDO::set);

            // 备注：这里不会为 null，因为：roomCurrentDOList 已经进行过 size() == 0 的判断了
            GameRoomCurrentDO gameRoomCurrentDO = atomicGameRoomCurrentDO.get();

            if (gameRoomCurrentDO.getCurrentConnectTotal() < gameRoomConfigDO.getMaxUserTotal()) {
                // 直接加入该 房间
                return joinGameRoomCurrent(currentUserId, gameSocketServerDOList, gameRoomCurrentDO, currentGameUserId,
                    gameRoomConfigDO);
            } else {
                // 这里是人数超过房间人数上限的情况，则判断是否可以创建新的房间，如果可以，则创建
                if (gameRoomConfigDO.getMaxRoomTotal() > roomCurrentDOList.size()) {
                    log.info("人数超过房间人数上限，创建新的房间");
                    // 创建所有，通过：连接数最少的 socket服务器
                    return createAllByMinConnectSocketServer(dto, currentUserId, gameSocketServerDOList,
                        currentGameUserId);
                } else {
                    ApiResultVO.error("操作失败：房间人数已满，请稍后再试");
                }
            }

            return null; // 备注：这里不会执行，只是为了语法检查

        });
    }

    /**
     * 直接加入房间
     */
    @NotNull
    private GameSocketServerDO joinGameRoomCurrent(Long currentUserId, List<GameSocketServerDO> gameSocketServerDOList,
        GameRoomCurrentDO gameRoomCurrentDO, Long currentGameUserId, GameRoomConfigDO gameRoomConfigDO) {

        GameUserConnectDO gameUserConnectDO = new GameUserConnectDO();
        gameUserConnectDO.setGameUserId(currentGameUserId);
        gameUserConnectDO.setRoomCurrentId(gameRoomCurrentDO.getId());
        gameUserConnectDO.setUserId(currentUserId);
        gameUserConnectService.save(gameUserConnectDO); // 保存到：数据库

        Optional<GameSocketServerDO> findFirstOptional =
            gameSocketServerDOList.stream().filter(it -> it.getId().equals(gameRoomCurrentDO.getSocketServerId()))
                .findFirst();

        if (findFirstOptional.isPresent()) {

            GameSocketServerDO gameSocketServerDO = findFirstOptional.get();
            log.info("直接加入该 房间，房间 id：{}，socket服务器信息：{}", gameRoomCurrentDO.getId(), gameSocketServerDO);

            return gameSocketServerDO;

        } else {

            // 获取一个新的 socket服务器
            return reconnectRoomGetNewSocketServer(gameRoomCurrentDO, gameRoomConfigDO, currentGameUserId,
                gameSocketServerDOList);

        }

    }

    /**
     * 创建所有，通过：连接数最少的 socket服务器
     */
    @NotNull
    private GameSocketServerDO createAllByMinConnectSocketServer(GameRoomCurrentJoinRoomDTO dto, Long currentUserId,
        List<GameSocketServerDO> gameSocketServerDOList, Long currentGameUserId) {

        // 找到：连接数最少的 socket服务器
        GameSocketServerDO minConnectSocketServerDO = getMinConnectSocketServerDO(gameSocketServerDOList);

        GameRoomCurrentDO gameRoomCurrentDO = new GameRoomCurrentDO();
        gameRoomCurrentDO.setRoomConfigId(dto.getRoomConfigId());
        gameRoomCurrentDO.setSocketServerId(minConnectSocketServerDO.getId());

        save(gameRoomCurrentDO); // 保存到：数据库

        GameUserConnectDO gameUserConnectDO = new GameUserConnectDO();
        gameUserConnectDO.setGameUserId(currentGameUserId);
        gameUserConnectDO.setRoomCurrentId(gameRoomCurrentDO.getId());
        gameUserConnectDO.setUserId(currentUserId);

        gameUserConnectService.save(gameUserConnectDO); // 保存到：数据库

        return minConnectSocketServerDO;
    }

    /**
     * 找到：连接数最少的 socket服务器
     */
    private GameSocketServerDO getMinConnectSocketServerDO(List<GameSocketServerDO> gameSocketServerDOList) {

        AtomicReference<GameSocketServerDO> atomicGameSocketServerDO = new AtomicReference<>();

        gameSocketServerDOList.stream()
            .min(Comparator.comparing(GameSocketServerDO::getSocketServerCurrentConnectTotal))
            .ifPresent(atomicGameSocketServerDO::set);

        GameSocketServerDO gameSocketServerDO = atomicGameSocketServerDO.get();

        if (gameSocketServerDO == null) {
            ApiResultVO.sysError();
        }

        log.info("连接数最少的 socket服务器：{}", gameSocketServerDO);

        if (gameSocketServerDO.getSocketServerCurrentConnectTotal() >= gameSocketServerDO.getMaxConnect()) {
            ApiResultVO.error("操作失败：socket服务器连接数已满，请稍后重试");
        }

        return gameSocketServerDO;
    }

    /**
     * 组装：连接数，并获取：当前房间配置下的 房间集合
     */
    @NotNull
    private List<GameRoomCurrentDO> setConnectTotalAndGetConfigRoomCurrentList(
        List<GameRoomCurrentDO> gameRoomCurrentDOList, List<GameSocketServerDO> gameSocketServerDOList,
        GameRoomConfigDO gameRoomConfigDO) {

        Set<Long> roomCurrentIdSet =
            gameRoomCurrentDOList.stream().map(GameRoomCurrentDO::getId).collect(Collectors.toSet());

        // 获取：每个：当前房间的连接数
        Map<Long, Long> roomCurrentConnectMap = getRoomCurrentConnectMapByRoomCurrentIdSet(roomCurrentIdSet);

        long roomCurrentTotal = 0; // 房间配置的 房间数
        long connectTotal = 0; // 房间配置的 当前连接数

        List<GameRoomCurrentDO> roomCurrentList = new ArrayList<>(); // 当前房间配置下的 房间集合

        // 组装到：每个房间对象里面
        for (GameRoomCurrentDO item : gameRoomCurrentDOList) {
            item.setCurrentConnectTotal(roomCurrentConnectMap.getOrDefault(item.getId(), 0L));
            if (item.getRoomConfigId().equals(gameRoomConfigDO.getId())) {
                roomCurrentTotal = roomCurrentTotal + 1; // 累加：房间数
                connectTotal = connectTotal + item.getCurrentConnectTotal(); // 累加：连接数
                roomCurrentList.add(item);
            }
        }

        Map<Long, Long> socketServerConnectMap = gameRoomCurrentDOList.stream().collect(Collectors
            .groupingBy(GameRoomCurrentDO::getSocketServerId,
                Collectors.summingLong(GameRoomCurrentDO::getCurrentConnectTotal)));

        // 组装：每个 socket服务器的连接数
        for (GameSocketServerDO item : gameSocketServerDOList) {
            item.setSocketServerCurrentConnectTotal(socketServerConnectMap.getOrDefault(item.getId(), 0L));
        }

        // 设置：房间配置的，连接数，当前房间数
        gameRoomConfigDO.setConnectTotal(connectTotal);
        gameRoomConfigDO.setRoomCurrentTotal(roomCurrentTotal);

        log.info("房间配置的，当前连接数：{}，当前房间数：{}", connectTotal, roomCurrentTotal);

        return roomCurrentList;

    }

    /**
     * 获取：每个：当前房间的连接数
     */
    @NotNull
    private Map<Long, Long> getRoomCurrentConnectMapByRoomCurrentIdSet(Set<Long> roomCurrentIdSet) {

        // 每个：房间的连接数
        List<GameUserConnectDO> gameUserConnectDOList;
        if (roomCurrentIdSet.size() == 0) {
            gameUserConnectDOList = new ArrayList<>();
        } else {
            gameUserConnectDOList =
                gameUserConnectService.query().select(" room_current_id, count(*) as roomCurrentConnectTotal ")
                    .in("room_current_id", roomCurrentIdSet).groupBy("room_current_id").list();
        }

        return gameUserConnectDOList.stream().collect(
            Collectors.toMap(GameUserConnectDO::getRoomCurrentId, GameUserConnectDO::getRoomCurrentConnectTotal));

    }

    /**
     * 加入房间：拿到：返回值
     */
    @NotNull
    private GameRoomCurrentJoinRoomVO getGameRoomCurrentJoinRoomVO(Long currentUserId,
        GameSocketServerDO gameSocketServerDO, Long currentGameUserId) {

        String uuid = IdUtil.simpleUUID();

        // 存储：连接码到 redis里
        redissonClient.getBucket(SocketServerRedisKeyEnum.PRE_NETTY_TCP_PROTO_BUF_CONNECT_SECURITY_CODE + uuid)
            .set(currentUserId + GameAuthConstant.AUTH_SEPARATOR + currentGameUserId,
                BaseConstant.SHORT_CODE_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        GameRoomCurrentJoinRoomVO gameRoomCurrentJoinRoomVO = new GameRoomCurrentJoinRoomVO();
        gameRoomCurrentJoinRoomVO.setHost(gameSocketServerDO.getHost());
        gameRoomCurrentJoinRoomVO.setPort(gameSocketServerDO.getPort());
        gameRoomCurrentJoinRoomVO.setSecurityCode(uuid);

        return gameRoomCurrentJoinRoomVO;

    }

    /**
     * 重连房间
     * 备注：不要加事务，因为：无法重连时要：移除 不可用的数据
     */
    @Nullable
    private GameRoomCurrentJoinRoomVO reconnectRoom(Long currentUserId, Long currentGameUserId) {

        GameUserConnectDO gameUserConnectDO =
            gameUserConnectService.lambdaQuery().select(GameUserConnectDO::getRoomCurrentId)
                .eq(GameUserConnectDO::getGameUserId, currentGameUserId).one();
        if (gameUserConnectDO == null) {
            log.info("没有连接信息，无法重连");
            return null;
        }

        GameRoomCurrentDO gameRoomCurrentDO =
            lambdaQuery().eq(GameRoomCurrentDO::getId, gameUserConnectDO.getRoomCurrentId())
                .select(GameRoomCurrentDO::getSocketServerId, GameRoomCurrentDO::getRoomConfigId,
                    GameRoomCurrentDO::getId).one();
        if (gameRoomCurrentDO == null) {
            reconnectRoomRemoveInvalidData(currentGameUserId, null, 1); // 移除：不可用的数据
            log.info("没有找到 当前房间信息，无法重连");
            return null;
        }

        GameRoomConfigDO gameRoomConfigDO = gameRoomConfigService.lambdaQuery().select(GameRoomConfigDO::getPlayType)
            .eq(BaseEntity::getId, gameRoomCurrentDO.getRoomConfigId()).one();
        if (gameRoomConfigDO == null) {
            reconnectRoomRemoveInvalidData(currentGameUserId, gameRoomCurrentDO, 2); // 移除：不可用的数据
            log.info("没有找到 房间配置信息，无法重连");
            return null;
        }

        if (gameRoomConfigDO.getPlayType().equals(GameRoomConfigPlayTypeEnum.HALL)) {
            reconnectRoomRemoveInvalidData(currentGameUserId, gameRoomCurrentDO, 1); // 移除：不可用的数据
            log.info("大厅类型房间，无法重连");
            return null;
        }

        GameSocketServerDO gameSocketServerDO =
            gameSocketServerService.lambdaQuery().eq(BaseEntity::getId, gameRoomCurrentDO.getSocketServerId())
                .select(GameSocketServerDO::getHost, GameSocketServerDO::getPort).one();

        if (gameSocketServerDO == null) {
            // 获取一个新的 socket服务器
            gameSocketServerDO =
                reconnectRoomGetNewSocketServer(gameRoomCurrentDO, gameRoomConfigDO, currentGameUserId, null);
        }

        log.info("用户重连成功，游戏用户主键 id：{}，socket服务器信息：{}", currentGameUserId, gameSocketServerDO);
        return getGameRoomCurrentJoinRoomVO(currentUserId, gameSocketServerDO, currentGameUserId);

    }

    /**
     * 重连房间，获取一个新的 socket服务器
     */
    @NotNull
    private GameSocketServerDO reconnectRoomGetNewSocketServer(GameRoomCurrentDO gameRoomCurrentDO,
        GameRoomConfigDO gameRoomConfigDO, Long currentGameUserId, List<GameSocketServerDO> gameSocketServerDOList) {

        if (gameSocketServerDOList == null) {
            // 获取：所有的 socket服务器
            gameSocketServerDOList = gameSocketServerService.lambdaQuery()
                .select(GameSocketServerDO::getId, GameSocketServerDO::getHost, GameSocketServerDO::getPort,
                    GameSocketServerDO::getMaxConnect).list();
        }

        if (gameSocketServerDOList.size() == 0) {
            ApiResultVO.error("操作失败：找不到 socket服务器，请联系管理员");
        }

        List<GameSocketServerDO> finalGameSocketServerDOList = gameSocketServerDOList;

        // 上锁：防止重复指定 socket服务器
        return RedissonUtil
            .doLock(SocketServerRedisKeyEnum.PRE_RECONNECT_CURRENT_ROOM_ID.name() + gameRoomCurrentDO.getId(), () -> {

                // 再获取一次，是否已经存在 socket服务器
                GameRoomCurrentDO newRoomCurrentDO =
                    lambdaQuery().eq(GameRoomCurrentDO::getId, gameRoomCurrentDO.getId())
                        .select(GameRoomCurrentDO::getSocketServerId, GameRoomCurrentDO::getRoomConfigId,
                            GameRoomCurrentDO::getId).one();

                GameSocketServerDO newSocketServerDO =
                    gameSocketServerService.lambdaQuery().eq(BaseEntity::getId, newRoomCurrentDO.getSocketServerId())
                        .select(GameSocketServerDO::getHost, GameSocketServerDO::getPort).one();

                if (newSocketServerDO != null) {
                    log.info("用户重连成功，获取到 socket服务器，游戏用户主键 id：{}", currentGameUserId);
                    return newSocketServerDO;
                }

                // 获取：所有的 当前房间
                List<GameRoomCurrentDO> gameRoomCurrentDOList = getAllGameRoomCurrentDOList();

                // 组装：连接数
                setConnectTotalAndGetConfigRoomCurrentList(gameRoomCurrentDOList, finalGameSocketServerDOList,
                    gameRoomConfigDO);

                // 找到：连接数最少的 socket服务器
                GameSocketServerDO minConnectSocketServerDO = getMinConnectSocketServerDO(finalGameSocketServerDOList);

                log.info("用户重连：原来的 socket服务器不存在，重新获取一个，socket服务器信息：{}", minConnectSocketServerDO);

                gameRoomCurrentDO.setSocketServerId(minConnectSocketServerDO.getId());
                updateById(gameRoomCurrentDO); // 更新数据库：新的 socket服务器 id

                return minConnectSocketServerDO;

            });

    }

    /**
     * 获取：所有的 当前房间
     */
    @NotNull
    private List<GameRoomCurrentDO> getAllGameRoomCurrentDOList() {

        // 获取：所有的 当前房间
        List<GameRoomCurrentDO> gameRoomCurrentDOList = lambdaQuery()
            .select(GameRoomCurrentDO::getId, GameRoomCurrentDO::getRoomConfigId, GameRoomCurrentDO::getSocketServerId)
            .list();

        if (gameRoomCurrentDOList.size() != 0) {
            // 处理：gameRoomCurrentDOList，移除一些不可用的房间
            gameRoomCurrentDOList = handlerGameRoomCurrentDOList(gameRoomCurrentDOList);
        }

        return gameRoomCurrentDOList;

    }

    /**
     * 处理：gameRoomCurrentDOList，移除一些不可用的房间
     */
    @NotNull
    private List<GameRoomCurrentDO> handlerGameRoomCurrentDOList(List<GameRoomCurrentDO> gameRoomCurrentDOList) {

        // 移除：房间配置不存在的 当前房间
        List<GameRoomConfigDO> gameRoomConfigDOList =
            gameRoomConfigService.lambdaQuery().select(BaseEntity::getId).list();
        Set<Long> roomConfigIdSet = gameRoomConfigDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        Set<Long> removeRoomCurrentIdSet =
            gameRoomCurrentDOList.stream().filter(it -> !roomConfigIdSet.contains(it.getRoomConfigId()))
                .map(GameRoomCurrentDO::getId).collect(Collectors.toSet());

        if (removeRoomCurrentIdSet.size() != 0) {

            log.info("移除：房间配置，不存在的 当前房间 idSet：{}", removeRoomCurrentIdSet);
            removeByIds(removeRoomCurrentIdSet);

            gameRoomCurrentDOList =
                gameRoomCurrentDOList.stream().filter(it -> !removeRoomCurrentIdSet.contains(it.getId()))
                    .collect(Collectors.toList());

        }

        return gameRoomCurrentDOList;

    }

    /**
     * 重连房间：移除 不可用的数据
     */
    private void reconnectRoomRemoveInvalidData(Long currentGameUserId, GameRoomCurrentDO gameRoomCurrentDO, int type) {

        if (type >= 1) {
            // 移除：不可用的数据
            gameUserConnectService.lambdaUpdate().eq(GameUserConnectDO::getGameUserId, currentGameUserId).remove();
        }

        if (type >= 2) {
            // 移除：不可用的数据
            lambdaUpdate().eq(GameRoomCurrentDO::getSocketServerId, gameRoomCurrentDO.getSocketServerId()).remove();
        }

    }

    /**
     * 退出房间
     */
    @Override
    public String exitRoom() {

        // 移除：不可用的数据
        reconnectRoomRemoveInvalidData(GameAuthUserUtil.getCurrentGameUserId(), null, 1);

        return BaseBizCodeEnum.OK;
    }

}
