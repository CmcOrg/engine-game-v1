package com.cmcorg.engine.game.netty.tcp.protobuf.configuration;

import com.cmcorg.engine.game.room.config.model.enums.GameRoomConfigRoomTypeEnum;
import com.cmcorg.engine.game.room.current.model.bo.GameRoomCurrentJoinRoomRedisBO;

import java.util.Set;

public interface IAcceptRoomTypeConfiguration {

    /**
     * 支持的房间类型 set
     */
    Set<GameRoomConfigRoomTypeEnum> acceptRoomTypeSet();

    /**
     * 处理 gameRoomCurrentJoinRoomRedisBO
     */
    void handlerGameRoomCurrentJoinRoomRedisBO(GameRoomCurrentJoinRoomRedisBO gameRoomCurrentJoinRoomRedisBO);

}
