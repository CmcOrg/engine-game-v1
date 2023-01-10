package com.cmcorg.engine.game.auth.model.bo;

import com.cmcorg.engine.game.auth.model.entity.GameRoomConfigDO;
import com.cmcorg.engine.game.auth.model.entity.GameRoomCurrentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BasePlayerBO {

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "游戏用户主键 id")
    private Long gameUserId;

    @Schema(description = "用户当前房间的信息")
    private GameRoomCurrentDO gameRoomCurrentDO;

    @Schema(description = "用户当前房间的 配置信息")
    private GameRoomConfigDO gameRoomConfigDO;

    @Schema(description = "是否可用")
    private Boolean enableFlag;

}
