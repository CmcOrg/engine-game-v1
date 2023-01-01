package com.cmcorg.engine.game.user.exp.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg.engine.game.auth.model.enums.GameUserExpTypeEnum;
import com.cmcorg.engine.web.auth.model.entity.BaseEntityNoId;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestClass;
import com.cmcorg.engine.web.model.generate.model.constant.WebModelConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@RequestClass(tableIgnoreFields = WebModelConstant.TABLE_IGNORE_FIELDS_TWO)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "game_user_exp")
@Data
@Schema(description = "子表：用户经验值，主表：用户")
public class GameUserExpDO extends BaseEntityNoId {

    @TableId
    @Schema(description = "用户主键 id")
    private Long id;

    @Schema(description = "经验值类型：1 普通经验")
    private GameUserExpTypeEnum type;

    @Schema(description = "经验值")
    private Long value;

}
