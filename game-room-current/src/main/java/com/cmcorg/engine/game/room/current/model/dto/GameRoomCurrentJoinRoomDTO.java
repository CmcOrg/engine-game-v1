package com.cmcorg.engine.game.room.current.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRoomCurrentJoinRoomDTO {

    @NotNull
    @Schema(description = "房间配置主键 id，备注：-1 表示：重连")
    private Long roomConfigId;

}
