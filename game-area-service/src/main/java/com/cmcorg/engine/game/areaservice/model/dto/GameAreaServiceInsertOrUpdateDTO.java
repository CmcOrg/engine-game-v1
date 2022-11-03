package com.cmcorg.engine.game.areaservice.model.dto;

import com.cmcorg.engine.game.areaservice.model.enums.GameAreaServiceStateEnum;
import com.cmcorg.engine.web.model.model.dto.BaseInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameAreaServiceInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @Size(max = 10)
    @NotBlank
    @Schema(description = "区服名称")
    private String name;

    @NotNull
    @Schema(description = "区服状态：0 关闭 1 正常 2 维护")
    private GameAreaServiceStateEnum state;

    @Schema(description = "备注")
    private String remark;

    @Min(1)
    @NotNull
    @Schema(description = "该区服下，每个用户最多可创建的角色个数")
    private Integer userGameUserMaxNumber;

}
