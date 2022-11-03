package com.cmcorg.engine.game.room.config.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "货币类型")
public enum GameMoneyTypeEnum {

    NONE(-1, "无"), //
    GOLD(1, "金币"), //
    DRAGON(2, "龙晶"), //
    DIAMOND(3, "钻石"), //
    TEMP(4, "临时货币"), // 退房间清零

    ;

    @EnumValue
    @JsonValue
    private int code;
    private String codeDescription; // code 说明

}
