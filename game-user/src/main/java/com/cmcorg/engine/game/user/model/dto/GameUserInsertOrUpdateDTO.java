package com.cmcorg.engine.game.user.model.dto;

import com.cmcorg.engine.web.model.generate.model.annotation.RequestField;
import com.cmcorg.engine.web.model.model.constant.BaseRegexConstant;
import com.cmcorg.engine.web.model.model.dto.BaseInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameUserInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @Pattern(regexp = BaseRegexConstant.NICK_NAME_REGEXP)
    @NotBlank
    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "头像uri")
    private String avatarUri;

    @Min(1)
    @NotNull
    @Schema(description = "区服主键 id（外键）")
    private Long areaServiceId;

    @Min(1)
    @NotNull
    @Schema(description = "用户主键id（外键）")
    private Long userId;

    @RequestField(formTitle = "是否正常")
    @Schema(description = "正常/冻结")
    private Boolean enableFlag;

}
