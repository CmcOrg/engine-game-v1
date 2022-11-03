package com.cmcorg.engine.game.areaservice.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg.engine.game.areaservice.model.enums.GameAreaServiceStateEnum;
import com.cmcorg.engine.web.auth.model.entity.BaseEntity;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestClass;
import com.cmcorg.engine.web.model.generate.model.constant.WebModelConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@RequestClass(tableIgnoreFields = WebModelConstant.TABLE_IGNORE_FIELDS_TWO)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "game_area_service")
@Data
@Schema(description = "主表：区服")
public class GameAreaServiceDO extends BaseEntity {

    @Schema(description = "区服名称")
    private String name;

    @Schema(description = "区服状态：0 关闭 1 正常 2 维护")
    private GameAreaServiceStateEnum state;

    @Schema(description = "该区服下，每个用户最多可创建的角色个数")
    private Integer userGameUserMaxNumber;

}
