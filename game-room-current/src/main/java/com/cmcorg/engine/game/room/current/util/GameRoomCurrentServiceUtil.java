package com.cmcorg.engine.game.room.current.util;

import com.cmcorg.engine.game.auth.model.bo.GameCurrentRoomBO;
import com.cmcorg.engine.game.auth.model.entity.GameRoomCurrentDO;
import com.cmcorg.engine.game.room.current.service.GameRoomCurrentService;
import com.cmcorg.engine.game.user.connect.model.entity.GameUserConnectDO;
import com.cmcorg.engine.game.user.connect.service.GameUserConnectService;
import org.springframework.stereotype.Component;

@Component
public class GameRoomCurrentServiceUtil {

    private static GameUserConnectService gameUserConnectService;
    private static GameRoomCurrentService gameRoomCurrentService;

    public GameRoomCurrentServiceUtil(GameUserConnectService gameUserConnectService,
        GameRoomCurrentService gameRoomCurrentService) {
        GameRoomCurrentServiceUtil.gameUserConnectService = gameUserConnectService;
        GameRoomCurrentServiceUtil.gameRoomCurrentService = gameRoomCurrentService;
    }

    /**
     * 重连房间：移除 不可用的数据
     */
    public static void reconnectRoomRemoveInvalidData(Long currentGameUserId, GameRoomCurrentDO gameRoomCurrentDO,
        int type) {

        if (type >= 1) {
            // 移除：不可用的数据
            gameUserConnectService.lambdaUpdate().eq(GameUserConnectDO::getGameUserId, currentGameUserId).remove();
        }

        if (type >= 2) {
            // 移除：不可用的数据
            gameRoomCurrentService.lambdaUpdate()
                .eq(GameRoomCurrentDO::getSocketServerId, gameRoomCurrentDO.getSocketServerId()).remove();
        }

    }

    /**
     * 判断是否在房间里
     */
    public static boolean inRoomCheck(GameCurrentRoomBO gameCurrentRoomBO) {

        if (gameCurrentRoomBO == null) {
            return false;
        }

        return gameUserConnectService.lambdaQuery()
            .eq(GameUserConnectDO::getGameUserId, gameCurrentRoomBO.getGameUserId())
            .eq(GameUserConnectDO::getRoomCurrentId, gameCurrentRoomBO.getGameRoomCurrentDO().getId()).exists();

    }

}
