package com.cmcorg.engine.game.socket.server.model.enums;

import com.cmcorg.engine.web.redisson.model.interfaces.IRedisKey;

/**
 * redis中 key的枚举类
 * 备注：如果是 redisson的锁 key，一定要备注：锁什么，例如：锁【用户主键 id】
 * 备注：【PRE_】开头，表示 key后面还要跟字符串
 * 备注：【_CACHE】结尾，表示 key后面不用跟字符串
 */
public enum GameRedisKeyEnum implements IRedisKey {

    // 【PRE_】开头 ↓
    PRE_JOIN_ROOM_GAME_USER_ID, // 加入房间时，锁【游戏用户主键 id】

    PRE_RECONNECT_CURRENT_ROOM_ID, // 重连时，当前房间主键 id锁，锁【主键 id】

    PRE_SOCKET_AUTH_GAME_USER_ID, // socket 认证时的 gameUserId锁，锁【gameUserId】

    PRE_ROOM_CONFIG_ID, // 房间配置主键 id锁，锁【主键 id】

    PRE_NETTY_TCP_PROTO_BUF_CONNECT_SECURITY_CODE, // netty tcp protoBuf 连接时的身份认证 code前缀

    // 【_CACHE】结尾 ↓

    // 其他 ↓

    ;

}
