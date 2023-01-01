package com.cmcorg.engine.game.auth.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import com.cmcorg.engine.game.auth.configuration.GameJwtValidatorConfiguration;
import com.cmcorg.engine.game.auth.model.bo.GameRoomCurrentJoinRoomRedisBO;
import com.cmcorg.engine.game.model.model.constant.NettyTcpProtoBufServerKeyConstant;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.auth.util.AuthUserUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class GameAuthUserUtil {

    /**
     * 获取当前 gameUserId
     * 这里只会返回实际的 gameUserId，如果为 null，则会抛出异常
     */
    @NotNull
    public static Long getCurrentGameUserId() {

        Long gameUserId = getCurrentGameUserIdWillNull();

        if (gameUserId == null) {
            ApiResultVO.error(BaseBizCodeEnum.NOT_LOGGED_IN_YET);
        }

        return gameUserId;
    }

    /**
     * 获取当前 gameUserId，注意：这里获取 gameUserId之后需要做 非空判断
     * 这里只会返回实际的 gameUserId或者 null
     */
    @Nullable
    private static Long getCurrentGameUserIdWillNull() {

        return Convert.toLong(AuthUserUtil.getSecurityContextHolderContextAuthenticationPrincipalJsonObjectValueByKey(
            GameJwtValidatorConfiguration.PAYLOAD_MAP_GAME_USER_ID_KEY));

    }

    /**
     * 获取 GameRoomCurrentJoinRoomRedisBO
     */
    @Nullable
    public static GameRoomCurrentJoinRoomRedisBO getGameRoomCurrentJoinRoomRedisBO() {

        JSONObject gameRoomCurrentJoinRoomRedisBOJson = AuthUserUtil
            .getSecurityContextHolderContextAuthenticationPrincipalJsonObjectValueByKey(
                NettyTcpProtoBufServerKeyConstant.GAME_ROOM_CURRENT_JOIN_ROOM_REDIS_BO_STR_KEY);

        return BeanUtil.toBean(gameRoomCurrentJoinRoomRedisBOJson, GameRoomCurrentJoinRoomRedisBO.class);

    }

}
