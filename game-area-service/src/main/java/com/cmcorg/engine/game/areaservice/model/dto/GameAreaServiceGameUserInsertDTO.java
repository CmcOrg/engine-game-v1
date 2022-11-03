package com.cmcorg.engine.game.areaservice.model.dto;

import com.cmcorg.engine.web.model.model.constant.BaseRegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class GameAreaServiceGameUserInsertDTO {

    @Pattern(regexp = BaseRegexConstant.NICK_NAME_REGEXP)
    @NotBlank
    @Schema(description = "昵称")
    private String nickname;

    @Min(1)
    @NotNull
    @Schema(description = "区服主键 id（外键）")
    private Long areaServiceId;

}
