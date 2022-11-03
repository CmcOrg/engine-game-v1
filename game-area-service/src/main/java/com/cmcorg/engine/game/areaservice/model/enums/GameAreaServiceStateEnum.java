package com.cmcorg.engine.game.areaservice.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "区服状态")
public enum GameAreaServiceStateEnum {

    SHUTDOWN((byte)0, "关闭"), //
    NORMAL((byte)1, "正常"), //
    MAINTAIN((byte)2, "维护"), //

    ;

    @EnumValue
    @JsonValue
    private byte code;
    private String codeDescription; // code 说明

}
