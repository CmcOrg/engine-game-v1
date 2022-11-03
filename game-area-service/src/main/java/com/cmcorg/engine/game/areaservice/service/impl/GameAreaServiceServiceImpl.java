package com.cmcorg.engine.game.areaservice.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg.engine.game.areaservice.mapper.GameAreaServiceMapper;
import com.cmcorg.engine.game.areaservice.model.dto.*;
import com.cmcorg.engine.game.areaservice.model.entity.GameAreaServiceDO;
import com.cmcorg.engine.game.areaservice.model.enums.GameAreaServiceStateEnum;
import com.cmcorg.engine.game.areaservice.service.GameAreaServiceService;
import com.cmcorg.engine.game.auth.configuration.GameJwtValidatorConfiguration;
import com.cmcorg.engine.game.user.model.dto.GameUserInsertOrUpdateDTO;
import com.cmcorg.engine.game.user.model.entity.GameUserDO;
import com.cmcorg.engine.game.user.service.GameUserService;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.model.entity.BaseEntity;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.auth.util.AuthUserUtil;
import com.cmcorg.engine.web.auth.util.MyEntityUtil;
import com.cmcorg.engine.web.auth.util.MyJwtUtil;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GameAreaServiceServiceImpl extends ServiceImpl<GameAreaServiceMapper, GameAreaServiceDO>
    implements GameAreaServiceService {

    @Resource
    GameUserService gameUserService;

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(GameAreaServiceInsertOrUpdateDTO dto) {

        GameAreaServiceDO gameAreaServiceDO = new GameAreaServiceDO();
        gameAreaServiceDO.setName(dto.getName());
        gameAreaServiceDO.setState(dto.getState());
        gameAreaServiceDO.setId(dto.getId());
        gameAreaServiceDO.setEnableFlag(true);
        gameAreaServiceDO.setDelFlag(false);
        gameAreaServiceDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        gameAreaServiceDO.setUserGameUserMaxNumber(dto.getUserGameUserMaxNumber());

        saveOrUpdate(gameAreaServiceDO);

        return BaseBizCodeEnum.OK;
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<GameAreaServiceDO> myPage(GameAreaServicePageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), GameAreaServiceDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
            .eq(dto.getState() != null, GameAreaServiceDO::getState, dto.getState())
            .orderByDesc(BaseEntity::getUpdateTime).page(dto.getPage(true));
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public GameAreaServiceDO infoById(NotNullId notNullId) {
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
     * 用户，分页排序查询
     */
    @Override
    public Page<GameAreaServiceDO> userPage(GameAreaServiceUserPageDTO dto) {

        return lambdaQuery()
            .select(BaseEntity::getId, GameAreaServiceDO::getName, GameAreaServiceDO::getUserGameUserMaxNumber)
            .like(StrUtil.isNotBlank(dto.getName()), GameAreaServiceDO::getName, dto.getName())
            .ne(GameAreaServiceDO::getState, GameAreaServiceStateEnum.SHUTDOWN).orderByDesc(BaseEntity::getUpdateTime)
            .page(dto.getPage(true));

    }

    /**
     * 用户，新增，游戏用户
     */
    @Override
    public String userGameUserInsert(GameAreaServiceGameUserInsertDTO dto) {

        Long currentUserId = AuthUserUtil.getCurrentUserId();

        // 判断：该用户当前区服下的游戏用户个数，是否超过配置值
        GameAreaServiceDO gameAreaServiceDO =
            lambdaQuery().select(GameAreaServiceDO::getUserGameUserMaxNumber, GameAreaServiceDO::getState)
                .eq(BaseEntity::getId, dto.getAreaServiceId()).one();

        if (gameAreaServiceDO == null) {
            ApiResultVO.error("操作失败：区服不存在");
        }

        if (gameAreaServiceDO.getState().equals(GameAreaServiceStateEnum.SHUTDOWN)) {
            ApiResultVO.error("操作失败：区服已关闭，请联系管理员");
        }

        // 现在用户该区服下，拥有的角色个数
        Long count = gameUserService.lambdaQuery().eq(GameUserDO::getAreaServiceId, dto.getAreaServiceId())
            .eq(GameUserDO::getUserId, currentUserId).count();

        if (count >= gameAreaServiceDO.getUserGameUserMaxNumber()) {
            ApiResultVO.error("操作失败：最多可创建【{}】个角色", gameAreaServiceDO.getUserGameUserMaxNumber());
        }

        GameUserInsertOrUpdateDTO gameUserInsertOrUpdateDTO = new GameUserInsertOrUpdateDTO();
        gameUserInsertOrUpdateDTO.setNickname(dto.getNickname());
        gameUserInsertOrUpdateDTO.setAreaServiceId(dto.getAreaServiceId());
        gameUserInsertOrUpdateDTO.setUserId(currentUserId);
        gameUserInsertOrUpdateDTO.setEnableFlag(true);

        return gameUserService.insertOrUpdate(gameUserInsertOrUpdateDTO);
    }

    /**
     * 用户，批量删除，游戏用户
     */
    @Override
    public String userGameUserDeleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        gameUserService.removeByIds(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.OK;
    }

    /**
     * 用户，分页排序查询，当前区服下的游戏用户
     */
    @Override
    public Page<GameUserDO> userGameUserPage(GameAreaServiceUserGameUserPageDTO dto) {

        Long currentUserId = AuthUserUtil.getCurrentUserId();

        return gameUserService.lambdaQuery()
            .select(BaseEntity::getId, GameUserDO::getNickname, GameUserDO::getAvatarUri)
            .eq(dto.getAreaServiceId() != null, GameUserDO::getAreaServiceId, dto.getAreaServiceId())
            .eq(GameUserDO::getUserId, currentUserId).orderByAsc(BaseEntity::getCreateTime).page(dto.getPage(true));

    }

    /**
     * 用户，获取当前区服下的游戏用户 jwt
     */
    @Override
    public String userGameUserJwt(GameAreaServiceUserGameUserJwtDTO dto) {

        Long currentUserId = AuthUserUtil.getCurrentUserId();

        // 判断：这个游戏用户，是否是当前用户的
        boolean exists = gameUserService.lambdaQuery().eq(GameUserDO::getUserId, currentUserId)
            .eq(BaseEntity::getId, dto.getGameUserId()).exists();
        if (BooleanUtil.isFalse(exists)) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        return MyJwtUtil.generateJwt(currentUserId, null, (payloadMap) -> payloadMap
            .set(GameJwtValidatorConfiguration.PAYLOAD_MAP_GAME_USER_ID_KEY, dto.getGameUserId()));
    }

}
