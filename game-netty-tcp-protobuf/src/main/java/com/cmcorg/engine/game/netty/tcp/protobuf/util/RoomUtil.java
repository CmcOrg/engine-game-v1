package com.cmcorg.engine.game.netty.tcp.protobuf.util;

import cn.hutool.core.util.BooleanUtil;
import com.cmcorg.engine.game.auth.model.bo.GameRoomCurrentJoinRoomRedisBO;
import com.cmcorg.engine.game.auth.model.enums.GameRoomConfigRoomTypeEnum;
import com.cmcorg.engine.game.auth.model.vo.NettyTcpProtoBufVO;
import com.cmcorg.engine.game.auth.util.GameAuthUserUtil;
import com.cmcorg.engine.game.netty.tcp.protobuf.server.NettyTcpProtoBufServerHandlerHelper;
import com.cmcorg.engine.game.room.current.service.impl.GameRoomCurrentServiceImpl;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 房间工具类
 */
@Component
public class RoomUtil {

    /**
     * 通用的，退出房间
     */
    public static void exitRoom(Set<GameRoomConfigRoomTypeEnum> acceptRoomTypeSet) {

        // 判断是否在房间里
        GameRoomCurrentJoinRoomRedisBO gameRoomCurrentJoinRoomRedisBO =
            GameAuthUserUtil.getGameRoomCurrentJoinRoomRedisBO();

        if (gameRoomCurrentJoinRoomRedisBO == null) {
            NettyTcpProtoBufVO.error("操作失败：不存在房间信息");
        }

        if (BooleanUtil
            .isFalse(acceptRoomTypeSet.contains(gameRoomCurrentJoinRoomRedisBO.getGameRoomConfigDO().getRoomType()))) {
            NettyTcpProtoBufVO.error("操作失败：不在房间里");
        }

        // 移除：不可用的数据
        GameRoomCurrentServiceImpl.reconnectRoomRemoveInvalidData(GameAuthUserUtil.getCurrentGameUserId(), null, 1);

        // 关闭当前通道
        NettyTcpProtoBufServerHandlerHelper.closeSelf();

    }

}
