package com.cmcorg.engine.game.user.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg.engine.game.user.mapper.GameUserMapper;
import com.cmcorg.engine.game.user.model.dto.GameUserInsertOrUpdateDTO;
import com.cmcorg.engine.game.user.model.dto.GameUserPageDTO;
import com.cmcorg.engine.game.user.model.entity.GameUserDO;
import com.cmcorg.engine.game.user.service.GameUserService;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.model.entity.BaseEntity;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.auth.util.MyEntityUtil;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;
import org.springframework.stereotype.Service;

@Service
public class GameUserServiceImpl extends ServiceImpl<GameUserMapper, GameUserDO> implements GameUserService {

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(GameUserInsertOrUpdateDTO dto) {

        // 同一个区服下的，昵称不能重复
        boolean exists = lambdaQuery().eq(GameUserDO::getAreaServiceId, dto.getAreaServiceId())
            .eq(GameUserDO::getNickname, dto.getNickname()).ne(dto.getId() != null, BaseEntity::getId, dto.getId())
            .exists();
        if (exists) {
            ApiResultVO.error("操作失败：昵称已经存在");
        }

        GameUserDO gameUserDO = new GameUserDO();
        gameUserDO.setNickname(dto.getNickname());
        gameUserDO.setBio(MyEntityUtil.getNotNullStr(dto.getBio()));
        gameUserDO.setAvatarUri(MyEntityUtil.getNotNullStr(dto.getAvatarUri()));
        gameUserDO.setAreaServiceId(dto.getAreaServiceId());
        gameUserDO.setUserId(dto.getUserId());
        gameUserDO.setId(dto.getId());
        gameUserDO.setEnableFlag(BooleanUtil.isTrue(dto.getEnableFlag()));
        gameUserDO.setDelFlag(false);
        gameUserDO.setRemark("");

        saveOrUpdate(gameUserDO);

        return BaseBizCodeEnum.OK;
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<GameUserDO> myPage(GameUserPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getNickname()), GameUserDO::getNickname, dto.getNickname())
            .like(StrUtil.isNotBlank(dto.getAvatarUri()), GameUserDO::getAvatarUri, dto.getAvatarUri())
            .eq(dto.getId() != null, GameUserDO::getId, dto.getId())
            .eq(dto.getAreaServiceId() != null, GameUserDO::getAreaServiceId, dto.getAreaServiceId())
            .eq(dto.getUserId() != null, GameUserDO::getUserId, dto.getUserId()).orderByDesc(BaseEntity::getUpdateTime)
            .page(dto.getPage(true));
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public GameUserDO infoById(NotNullId notNullId) {
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
}
