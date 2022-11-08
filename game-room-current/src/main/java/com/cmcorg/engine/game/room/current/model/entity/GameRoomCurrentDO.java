package com.cmcorg.engine.game.room.current.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@TableName(value = "game_room_current")
@Data
@Schema(description = "主表：当前房间")
public class GameRoomCurrentDO {

    /**
     * 这里是自定义的主键 id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键id")
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "房间配置主键 id")
    private Long roomConfigId;

    @Schema(description = "socket服务器主键 id")
    private Long socketServerId;

    @TableField(exist = false)
    @Schema(description = "当前连接数")
    private Long currentConnectTotal;

}
