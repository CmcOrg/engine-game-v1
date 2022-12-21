package com.cmcorg.engine.game.auth.configuration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import com.cmcorg.engine.web.auth.configuration.security.IJwtValidatorConfiguration;
import com.cmcorg.engine.web.auth.filter.JwtAuthorizationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

@Configuration
@Slf4j
public class GameJwtValidatorConfiguration implements IJwtValidatorConfiguration {

    // 游戏用户 id
    public static final String PAYLOAD_MAP_GAME_USER_ID_KEY = "gameUserId";
    public static final Class<Long> PAYLOAD_MAP_GAME_USER_ID_CLASS = Long.class;

    // 不进行校验的白名单 uri开头集合
    public static final List<String> WHITE_START_LIST = CollUtil.newArrayList("/sign/", "/sys/", "/user/self/");
    // 不进行校验的白名单 uriSet
    public static final Set<String> WHITE_SET = CollUtil.newHashSet();

    static {

        // uri前缀集合
        List<String> preUriList = CollUtil
            .newArrayList("/game/areaService", "/game/roomConfig", "/game/roomCurrent", "/game/user",
                "/game/userConnect", "/game/userExp", "/game/socketServer");

        // uri后缀集合
        List<String> sufUriList = CollUtil.newArrayList("/insertOrUpdate", "/page", "/infoById", "/deleteByIdSet");
        for (String item : preUriList) {
            for (String subItem : sufUriList) {
                WHITE_SET.add(item + subItem);
            }
        }

        for (String subItem : CollUtil
            .newArrayList("/user/page", "/user/gameUser/insert", "/user/gameUser/deleteByIdSet", "/user/gameUser/page",
                "/user/gameUser/jwt")) {
            WHITE_SET.add("/game/areaService" + subItem);
        }

        log.info("不进行校验的白名单 uri开头集合：{}", WHITE_START_LIST);
        log.info("不进行校验的白名单 uriSet：{}", WHITE_SET);

    }

    /**
     * 额外的，检查 jwt的方法
     */
    @Override
    public boolean validator(JWT jwt, String requestUri, HttpServletResponse response) {

        for (String item : WHITE_START_LIST) {
            if (requestUri.startsWith(item)) {
                return true;
            }
        }
        if (WHITE_SET.contains(requestUri)) {
            return true;
        }

        JSONObject claimsJson = jwt.getPayload().getClaimsJson();

        // 必须有：游戏用户 id
        Long gameUserId = claimsJson.get(PAYLOAD_MAP_GAME_USER_ID_KEY, PAYLOAD_MAP_GAME_USER_ID_CLASS);

        if (gameUserId == null) {
            JwtAuthorizationFilter.loginExpired(response);
            return false;
        }

        return true;

    }

}
