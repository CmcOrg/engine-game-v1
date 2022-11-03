package com.cmcorg.engine.game.room.current.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg.engine.game.room.current.model.dto.GameRoomCurrentJoinRoomDTO;
import com.cmcorg.engine.game.room.current.model.dto.GameRoomCurrentPageDTO;
import com.cmcorg.engine.game.room.current.model.entity.GameRoomCurrentDO;
import com.cmcorg.engine.game.room.current.model.vo.GameRoomCurrentJoinRoomVO;
import com.cmcorg.engine.game.room.current.model.vo.GameRoomCurrentPageVO;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;

public interface GameRoomCurrentService extends IService<GameRoomCurrentDO> {

    Page<GameRoomCurrentPageVO> myPage(GameRoomCurrentPageDTO dto);

    GameRoomCurrentDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    GameRoomCurrentJoinRoomVO joinRoom(GameRoomCurrentJoinRoomDTO dto);

    GameRoomCurrentJoinRoomVO reconnectRoom();

    String exitRoom();

}
