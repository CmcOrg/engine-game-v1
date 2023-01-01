package com.cmcorg.engine.game.room.current.model.vo;

import com.cmcorg.engine.game.auth.model.enums.GameRoomConfigRoomTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GameRoomCurrentJoinRoomVO {

    @Schema(description = "host")
    private String host;

    @Schema(description = "端口，备注：host + 端口，可以表示唯一标识")
    private Integer port;

    @Schema(description = "连接码：用于获取：用户主键 id，格式：simple-uuid")
    private String securityCode;

    @Schema(description = "当前所在的房间类型")
    private GameRoomConfigRoomTypeEnum currentRoomType;

}
