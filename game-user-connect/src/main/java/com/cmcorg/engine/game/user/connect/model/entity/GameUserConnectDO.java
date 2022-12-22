package com.cmcorg.engine.game.user.connect.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestClass;
import com.cmcorg.engine.web.model.generate.model.constant.WebModelConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@RequestClass(tableIgnoreFields = WebModelConstant.TABLE_IGNORE_FIELDS_TWO)
@TableName(value = "game_user_connect")
@Data
@Schema(description = "子表：用户连接，主表：用户")
public class GameUserConnectDO {

    @TableId(type = IdType.INPUT)
    @Schema(description = "游戏用户主键 id")
    private Long gameUserId;

    @Schema(description = "当前房间主键 id")
    private Long roomCurrentId;

    @Schema(description = "用户主键 id")
    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改时间")
    private Date updateTime;

    @TableField(exist = false)
    @Schema(description = "按照房间分组之后，每个当前房间的连接数")
    private Long roomCurrentConnectTotal;

}
