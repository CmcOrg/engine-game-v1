package com.cmcorg.engine.game.user.exp.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg.engine.game.user.exp.mapper.GameUserExpMapper;
import com.cmcorg.engine.game.user.exp.model.dto.GameUserExpPageDTO;
import com.cmcorg.engine.game.user.exp.model.entity.GameUserExpDO;
import com.cmcorg.engine.game.user.exp.service.GameUserExpService;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;
import org.springframework.stereotype.Service;

@Service
public class GameUserExpServiceImpl extends ServiceImpl<GameUserExpMapper, GameUserExpDO>
    implements GameUserExpService {

    /**
     * 分页排序查询
     */
    @Override
    public Page<GameUserExpDO> myPage(GameUserExpPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(Convert.toStr(dto.getId())), GameUserExpDO::getId, dto.getId())
            .eq(dto.getType() != null, GameUserExpDO::getType, dto.getType()).orderByDesc(GameUserExpDO::getUpdateTime)
            .page(dto.getPage(true));
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public GameUserExpDO infoById(NotNullId notNullId) {
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
