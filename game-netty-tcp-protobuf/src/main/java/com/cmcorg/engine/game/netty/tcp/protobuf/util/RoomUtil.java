package com.cmcorg.engine.game.netty.tcp.protobuf.util;

import cn.hutool.core.util.BooleanUtil;
import com.cmcorg.engine.game.auth.model.bo.GameCurrentRoomBO;
import com.cmcorg.engine.game.auth.model.enums.GameRoomConfigRoomTypeEnum;
import com.cmcorg.engine.game.auth.model.vo.NettyTcpProtoBufVO;
import com.cmcorg.engine.game.auth.util.GameAuthUserUtil;
import com.cmcorg.engine.game.netty.tcp.protobuf.server.NettyTcpProtoBufServerHandlerHelper;
import com.cmcorg.engine.game.room.current.util.GameRoomCurrentServiceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;

/**
 * 房间工具类
 */
@Component
public class RoomUtil {

    /**
     * 通用的，退出房间
     */
    public static void exitRoom(@NotNull Set<GameRoomConfigRoomTypeEnum> acceptRoomTypeSet,
        @Nullable Function<GameCurrentRoomBO, Boolean> function) {

        // 判断是否在房间里
        GameCurrentRoomBO gameCurrentRoomBO = GameAuthUserUtil.getGameCurrentRoomBO();

        if (gameCurrentRoomBO == null) {
            NettyTcpProtoBufVO.error("操作失败：不存在房间信息");
        }

        if (BooleanUtil.isFalse(acceptRoomTypeSet.contains(gameCurrentRoomBO.getGameRoomConfigDO().getRoomType()))) {
            NettyTcpProtoBufVO.error("操作失败：不在房间里");
        }

        // 关闭当前通道
        NettyTcpProtoBufServerHandlerHelper.closeSelf();

        Boolean removeInvalidDataFlag = false;

        if (function != null) {
            removeInvalidDataFlag = function.apply(gameCurrentRoomBO); // 进行额外的一些处理
        }

        if (BooleanUtil.isTrue(removeInvalidDataFlag)) {
            // 移除：不可用的数据
            GameRoomCurrentServiceUtil.reconnectRoomRemoveInvalidData(GameAuthUserUtil.getCurrentGameUserId(), null, 1);
        }

    }

}
