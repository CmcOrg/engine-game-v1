package com.cmcorg.engine.game.areaservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameAreaServiceUserGameUserJwtDTO {

    @Min(1)
    @NotNull
    @Schema(description = "游戏用户主键 id")
    private Long gameUserId;

}
