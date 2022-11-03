package com.cmcorg.engine.game.areaservice.model.dto;

import com.cmcorg.engine.web.auth.model.dto.MyPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameAreaServiceUserGameUserPageDTO extends MyPageDTO {

    @Schema(description = "区服主键 id（外键）")
    private Long areaServiceId;

}
