package com.cmcorg.engine.game.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg.engine.game.user.model.dto.GameUserInsertOrUpdateDTO;
import com.cmcorg.engine.game.user.model.dto.GameUserPageDTO;
import com.cmcorg.engine.game.user.model.entity.GameUserDO;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;

public interface GameUserService extends IService<GameUserDO> {

    String insertOrUpdate(GameUserInsertOrUpdateDTO dto);

    Page<GameUserDO> myPage(GameUserPageDTO dto);

    GameUserDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}
