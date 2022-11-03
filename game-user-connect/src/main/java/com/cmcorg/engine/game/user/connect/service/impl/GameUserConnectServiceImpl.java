package com.cmcorg.engine.game.user.connect.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg.engine.game.user.connect.mapper.GameUserConnectMapper;
import com.cmcorg.engine.game.user.connect.model.dto.GameUserConnectPageDTO;
import com.cmcorg.engine.game.user.connect.model.entity.GameUserConnectDO;
import com.cmcorg.engine.game.user.connect.service.GameUserConnectService;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;
import org.springframework.stereotype.Service;

@Service
public class GameUserConnectServiceImpl extends ServiceImpl<GameUserConnectMapper, GameUserConnectDO>
    implements GameUserConnectService {

    /**
     * 分页排序查询
     */
    @Override
    public Page<GameUserConnectDO> myPage(GameUserConnectPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(Convert.toStr(dto.getId())), GameUserConnectDO::getId, dto.getId())
            .like(StrUtil.isNotBlank(Convert.toStr(dto.getRoomCurrentId())), GameUserConnectDO::getRoomCurrentId,
                dto.getRoomCurrentId()).orderByDesc(GameUserConnectDO::getUpdateTime).page(dto.getPage(true));
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public GameUserConnectDO infoById(NotNullId notNullId) {
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
