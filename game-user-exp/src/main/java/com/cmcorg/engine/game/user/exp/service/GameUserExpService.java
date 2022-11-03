package com.cmcorg.engine.game.user.exp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg.engine.game.user.exp.model.dto.GameUserExpPageDTO;
import com.cmcorg.engine.game.user.exp.model.entity.GameUserExpDO;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;

public interface GameUserExpService extends IService<GameUserExpDO> {

    Page<GameUserExpDO> myPage(GameUserExpPageDTO dto);

    GameUserExpDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}
