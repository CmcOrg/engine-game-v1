package com.cmcorg.engine.game.user.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg.engine.web.auth.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "game_user")
@Data
@Schema(description = "子表：游戏用户，主表：用户")
public class GameUserDO extends BaseEntity {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "头像uri")
    private String avatarUri;

    @Schema(description = "区服主键 id（外键）")
    private Long areaServiceId;

    @Schema(description = "用户主键id（外键）")
    private Long userId;

}
