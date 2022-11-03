package com.cmcorg.engine.game.room.current.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRoomCurrentJoinRoomDTO {

    @Min(1)
    @NotNull
    @Schema(description = "房间配置主键 id")
    private Long roomConfigId;

}
