package com.cmcorg.engine.game.netty.tcp.protobuf.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NettyOtherPathEnum {

    COMMON_ERROR("/commonError"), // 通用异常 uri
    CONNECT_SECURITY("/connectSecurity"), // 处理：身份认证的消息 uri

    ;

    private String uri;

}
