package com.cmcorg.engine.game.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GameUserSelfInfoVO {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "头像uri")
    private String avatarUri;

    @Schema(description = "区服主键 id")
    private Long areaServiceId;

}
