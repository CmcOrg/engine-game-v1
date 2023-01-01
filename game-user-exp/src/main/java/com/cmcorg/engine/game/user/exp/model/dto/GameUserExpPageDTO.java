package com.cmcorg.engine.game.user.exp.model.dto;

import com.cmcorg.engine.game.auth.model.enums.GameUserExpTypeEnum;
import com.cmcorg.engine.web.auth.model.dto.MyPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameUserExpPageDTO extends MyPageDTO {

    @Schema(description = "用户主键 id")
    private Long id;

    @Schema(description = "经验值类型：1 普通经验")
    private GameUserExpTypeEnum type;

}
