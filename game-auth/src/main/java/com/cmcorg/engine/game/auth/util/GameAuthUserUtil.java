package com.cmcorg.engine.game.auth.util;

import cn.hutool.core.convert.Convert;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.auth.util.AuthUserUtil;
import com.cmcorg.engine.web.auth.util.MyJwtUtil;
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
            MyJwtUtil.PAYLOAD_MAP_USER_ID_KEY));
    }

}
