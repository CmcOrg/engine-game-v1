package com.cmcorg.engine.game.user.model.dto;

import com.cmcorg.engine.web.auth.model.dto.MyPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameUserPageDTO extends MyPageDTO {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像uri")
    private String avatarUri;

    @Schema(description = "区服主键 id（外键）")
    private Long areaServiceId;

    @Schema(description = "用户主键id（外键）")
    private Long userId;

}
