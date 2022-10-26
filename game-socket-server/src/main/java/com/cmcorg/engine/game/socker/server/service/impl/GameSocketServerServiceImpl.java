package com.cmcorg.engine.game.socker.server.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg.engine.game.socker.server.mapper.GameSocketServerMapper;
import com.cmcorg.engine.game.socker.server.model.dto.GameSocketServerPageDTO;
import com.cmcorg.engine.game.socker.server.model.entity.GameSocketServerDO;
import com.cmcorg.engine.game.socker.server.service.GameSocketServerService;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.model.entity.BaseEntity;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameSocketServerServiceImpl extends ServiceImpl<GameSocketServerMapper, GameSocketServerDO>
    implements GameSocketServerService {

    /**
     * 分页排序查询
     */
    @Override
    public Page<GameSocketServerDO> myPage(GameSocketServerPageDTO dto) {

        return lambdaQuery().eq(dto.getId() != null, BaseEntity::getId, dto.getId())
            .like(StrUtil.isNotBlank(dto.getHost()), GameSocketServerDO::getHost, dto.getHost())
            .eq(dto.getPort() != null, GameSocketServerDO::getPort, dto.getPort())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntity::getRemark, dto.getRemark())
            .orderByDesc(BaseEntity::getUpdateTime).page(dto.getPage(true));
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public GameSocketServerDO infoById(NotNullId notNullId) {
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
     * 新增：在启动 socket服务器之后
     */
    @Override
    @Transactional
    public void insertForStartSocketServer(GameSocketServerDO gameSocketServerDO) {

        // 先删除：ip + port 匹配的数据
        lambdaUpdate().eq(GameSocketServerDO::getHost, gameSocketServerDO.getHost())
            .eq(GameSocketServerDO::getPort, gameSocketServerDO.getPort()).remove();

        save(gameSocketServerDO);

    }

}
