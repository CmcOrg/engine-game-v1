package com.cmcorg.engine.game.areaservice.model.dto;

import com.cmcorg.engine.game.areaservice.model.enums.GameAreaServiceStateEnum;
import com.cmcorg.engine.web.auth.model.dto.MyPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameAreaServicePageDTO extends MyPageDTO {

    @Schema(description = "区服名称")
    private String name;

    @Schema(description = "区服状态：0 关闭 1 正常 2 维护")
    private GameAreaServiceStateEnum state;

    @Schema(description = "备注")
    private String remark;

}
