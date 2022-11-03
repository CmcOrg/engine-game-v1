package com.cmcorg.engine.game.room.config.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg.engine.game.room.config.model.dto.GameRoomConfigInsertOrUpdateDTO;
import com.cmcorg.engine.game.room.config.model.dto.GameRoomConfigPageDTO;
import com.cmcorg.engine.game.room.config.model.entity.GameRoomConfigDO;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;

public interface GameRoomConfigService extends IService<GameRoomConfigDO> {

    String insertOrUpdate(GameRoomConfigInsertOrUpdateDTO dto);

    Page<GameRoomConfigDO> myPage(GameRoomConfigPageDTO dto);

    GameRoomConfigDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    Page<GameRoomConfigDO> userPage();

}
