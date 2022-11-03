package com.cmcorg.engine.game.user.connect.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg.engine.game.user.connect.model.dto.GameUserConnectPageDTO;
import com.cmcorg.engine.game.user.connect.model.entity.GameUserConnectDO;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;

public interface GameUserConnectService extends IService<GameUserConnectDO> {

    Page<GameUserConnectDO> myPage(GameUserConnectPageDTO dto);

    GameUserConnectDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}
