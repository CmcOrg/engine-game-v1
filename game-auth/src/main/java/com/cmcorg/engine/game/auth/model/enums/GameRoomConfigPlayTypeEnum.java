package com.cmcorg.engine.game.auth.model.enums;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
@Schema(description = "房间玩法")
public enum GameRoomConfigPlayTypeEnum {

    HALL(1, "大厅", CollUtil.newHashSet(GameRoomConfigRoomTypeEnum.HALL_GENERAL)), //

    FISHING(2, "捕鱼", CollUtil
        .newHashSet(GameRoomConfigRoomTypeEnum.FISHING_ATTEMPT, GameRoomConfigRoomTypeEnum.FISHING_GENERAL,
            GameRoomConfigRoomTypeEnum.FISHING_CHALLENGE, GameRoomConfigRoomTypeEnum.FISHING_GRAND)), //

    FIGHTING_LANDLORD(3, "斗地主", CollUtil.newHashSet(GameRoomConfigRoomTypeEnum.FIGHTING_LANDLORD_GENERAL)), //

    ;

    @EnumValue
    @JsonValue
    private int code;
    private String codeDescription; // code 说明
    private Set<GameRoomConfigRoomTypeEnum> roomTypeSet; // 支持的房间类型

}
