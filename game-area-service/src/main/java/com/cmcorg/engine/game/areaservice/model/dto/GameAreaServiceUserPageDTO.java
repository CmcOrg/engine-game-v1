package com.cmcorg.engine.game.areaservice.model.dto;

import com.cmcorg.engine.web.auth.model.dto.MyPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameAreaServiceUserPageDTO extends MyPageDTO {

    @Schema(description = "区服名称")
    private String name;

}
