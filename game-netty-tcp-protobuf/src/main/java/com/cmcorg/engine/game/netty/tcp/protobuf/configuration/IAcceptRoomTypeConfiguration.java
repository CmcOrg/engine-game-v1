package com.cmcorg.engine.game.netty.tcp.protobuf.configuration;

import com.cmcorg.engine.game.auth.model.bo.GameRoomCurrentRoomBO;
import com.cmcorg.engine.game.auth.model.enums.GameRoomConfigRoomTypeEnum;

import java.util.Set;

public interface IAcceptRoomTypeConfiguration {

    /**
     * 支持的房间类型 set
     */
    Set<GameRoomConfigRoomTypeEnum> acceptRoomTypeSet();

    /**
     * 处理：gameRoomCurrentRoomBO
     */
    void handlerGameRoomCurrentRoomBO(GameRoomCurrentRoomBO gameRoomCurrentRoomBO);

}
