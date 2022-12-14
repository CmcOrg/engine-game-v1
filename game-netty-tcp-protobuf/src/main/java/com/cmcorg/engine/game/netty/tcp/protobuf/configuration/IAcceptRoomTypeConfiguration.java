package com.cmcorg.engine.game.netty.tcp.protobuf.configuration;

import com.cmcorg.engine.game.auth.model.bo.GameCurrentRoomBO;
import com.cmcorg.engine.game.auth.model.enums.GameRoomConfigRoomTypeEnum;

import java.util.Set;

public interface IAcceptRoomTypeConfiguration {

    /**
     * 支持的房间类型 set
     */
    Set<GameRoomConfigRoomTypeEnum> acceptRoomTypeSet();

    /**
     * 处理：gameCurrentRoomBO
     */
    void handlerGameCurrentRoomBO(GameCurrentRoomBO gameCurrentRoomBO);

}
