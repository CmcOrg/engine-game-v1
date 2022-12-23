package com.cmcorg.engine.game.room.config.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
@Schema(description = "房间类型")
public enum GameRoomConfigRoomTypeEnum {

    HALL_GENERAL(1000, "大厅-普通"), //

    FISHING_ATTEMPT(2000, "捕鱼-体验场"), //
    FISHING_GENERAL(2001, "捕鱼-普通场"), //
    FISHING_CHALLENGE(2002, "捕鱼-挑战场"), //
    FISHING_GRAND(2003, "捕鱼-大奖赛"), //

    FIGHTING_LANDLORD_GENERAL(3000, "斗地主-普通"), //

    ;

    @EnumValue
    @JsonValue
    private int code;
    private String codeDescription; // code 说明

    @Nullable
    public static GameRoomConfigRoomTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (GameRoomConfigRoomTypeEnum item : GameRoomConfigRoomTypeEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}
