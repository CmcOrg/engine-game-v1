package com.cmcorg.engine.game.netty.tcp.protobuf.configuration;

import com.cmcorg.engine.game.room.config.model.enums.GameRoomConfigRoomTypeEnum;
import io.netty.channel.Channel;

import java.util.Set;

public interface IAcceptRoomTypeConfiguration {

    /**
     * 支持的房间类型 set
     */
    Set<GameRoomConfigRoomTypeEnum> acceptRoomTypeSet();

    /**
     * 处理通道
     */
    void handlerChannel(Channel channel);

}
