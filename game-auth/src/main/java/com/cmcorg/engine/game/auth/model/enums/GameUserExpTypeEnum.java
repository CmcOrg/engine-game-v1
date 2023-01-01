package com.cmcorg.engine.game.auth.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "用户经验值类型")
public enum GameUserExpTypeEnum {

    NONE(-1, "无"), //
    GENERAL(1, "普通经验"), //

    ;

    @EnumValue
    @JsonValue
    private int code;
    private String codeDescription; // code 说明

}
