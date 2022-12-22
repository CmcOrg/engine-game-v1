package com.cmcorg.engine.game.user.connect.model.dto;

import com.cmcorg.engine.web.auth.model.dto.MyPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameUserConnectPageDTO extends MyPageDTO {

    @Schema(description = "游戏用户主键 id")
    private Long gameUserId;

    @Schema(description = "当前房间主键 id")
    private Long roomCurrentId;

    @Schema(description = "用户主键 id")
    private Long userId;

}
