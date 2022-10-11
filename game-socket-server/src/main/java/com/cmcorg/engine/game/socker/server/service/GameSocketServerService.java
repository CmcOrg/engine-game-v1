package com.cmcorg.engine.game.socker.server.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg.engine.game.socker.server.model.dto.GameSocketServerPageDTO;
import com.cmcorg.engine.game.socker.server.model.entity.GameSocketServerDO;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;

public interface GameSocketServerService extends IService<GameSocketServerDO> {

    Page<GameSocketServerDO> myPage(GameSocketServerPageDTO dto);

    GameSocketServerDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    void insertForStartSocketServer(GameSocketServerDO gameSocketServerDO);

}
