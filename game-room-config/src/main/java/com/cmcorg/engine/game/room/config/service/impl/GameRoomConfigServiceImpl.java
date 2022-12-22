package com.cmcorg.engine.game.room.config.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg.engine.game.room.config.mapper.GameRoomConfigMapper;
import com.cmcorg.engine.game.room.config.model.dto.GameRoomConfigInsertOrUpdateDTO;
import com.cmcorg.engine.game.room.config.model.dto.GameRoomConfigPageDTO;
import com.cmcorg.engine.game.room.config.model.entity.GameRoomConfigDO;
import com.cmcorg.engine.game.room.config.service.GameRoomConfigService;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.model.entity.BaseEntity;
import com.cmcorg.engine.web.auth.model.entity.BaseEntityNoId;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.auth.util.MyEntityUtil;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;
import org.springframework.stereotype.Service;

@Service
public class GameRoomConfigServiceImpl extends ServiceImpl<GameRoomConfigMapper, GameRoomConfigDO>
    implements GameRoomConfigService {

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(GameRoomConfigInsertOrUpdateDTO dto) {

        // 检查：房间类型是否合法
        if (!dto.getPlayType().getRoomTypeSet().contains(dto.getRoomType())) {
            ApiResultVO.error("操作失败：房间玩法和类型不匹配");
        }

        GameRoomConfigDO gameRoomConfigDO = new GameRoomConfigDO();
        gameRoomConfigDO.setName(dto.getName());
        gameRoomConfigDO.setOrderNo(MyEntityUtil.getNotNullOrderNo(dto.getOrderNo()));
        gameRoomConfigDO.setMaxUserTotal(MyEntityUtil.getNotNullInt(dto.getMaxUserTotal()));
        gameRoomConfigDO.setMaxRoomTotal(MyEntityUtil.getNotNullInt(dto.getMaxRoomTotal()));
        gameRoomConfigDO.setPlayType(dto.getPlayType());
        gameRoomConfigDO.setRoomType(dto.getRoomType());
        gameRoomConfigDO.setUseMoneyType(dto.getUseMoneyType());
        gameRoomConfigDO.setGotMoneyType(dto.getGotMoneyType());
        gameRoomConfigDO.setLimitMoneyType(dto.getLimitMoneyType());
        gameRoomConfigDO.setMinUserMoney(MyEntityUtil.getNotNullBigDecimal(dto.getMinUserMoney()));
        gameRoomConfigDO.setMaxUserMoney(MyEntityUtil.getNotNullBigDecimal(dto.getMaxUserMoney()));
        gameRoomConfigDO.setLimitExpType(dto.getLimitExpType());
        gameRoomConfigDO.setMinUserExp(MyEntityUtil.getNotNullBigDecimal(dto.getMinUserExp()));
        gameRoomConfigDO.setMaxUserExp(MyEntityUtil.getNotNullBigDecimal(dto.getMaxUserExp()));
        gameRoomConfigDO.setRoomExpType(dto.getRoomExpType());
        gameRoomConfigDO.setId(dto.getId());
        gameRoomConfigDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        gameRoomConfigDO.setDelFlag(false);
        gameRoomConfigDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        saveOrUpdate(gameRoomConfigDO);

        return BaseBizCodeEnum.OK;
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<GameRoomConfigDO> myPage(GameRoomConfigPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), GameRoomConfigDO::getName, dto.getName())
            .eq(dto.getPlayType() != null, GameRoomConfigDO::getPlayType, dto.getPlayType())
            .eq(dto.getRoomType() != null, GameRoomConfigDO::getRoomType, dto.getRoomType())
            .eq(dto.getUseMoneyType() != null, GameRoomConfigDO::getUseMoneyType, dto.getUseMoneyType())
            .eq(dto.getGotMoneyType() != null, GameRoomConfigDO::getGotMoneyType, dto.getGotMoneyType())
            .eq(dto.getLimitMoneyType() != null, GameRoomConfigDO::getLimitMoneyType, dto.getLimitMoneyType())
            .eq(dto.getLimitExpType() != null, GameRoomConfigDO::getLimitExpType, dto.getLimitExpType())
            .eq(dto.getRoomExpType() != null, GameRoomConfigDO::getRoomExpType, dto.getRoomExpType())
            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
            .orderByDesc(GameRoomConfigDO::getOrderNo).page(dto.getPage(true));
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public GameRoomConfigDO infoById(NotNullId notNullId) {
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
    public Page<GameRoomConfigDO> userPage() {

        return lambdaQuery().select(BaseEntity::getId, GameRoomConfigDO::getName, GameRoomConfigDO::getPlayType,
            GameRoomConfigDO::getRoomType, GameRoomConfigDO::getGotMoneyType, GameRoomConfigDO::getRoomExpType)
            .eq(BaseEntityNoId::getEnableFlag, true).orderByDesc(GameRoomConfigDO::getOrderNo).page(new Page<>(1, -1));

    }

}
