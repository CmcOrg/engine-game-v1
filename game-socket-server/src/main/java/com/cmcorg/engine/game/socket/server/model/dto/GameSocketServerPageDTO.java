package com.cmcorg.engine.game.socket.server.model.dto;

import com.cmcorg.engine.web.auth.model.dto.MyPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameSocketServerPageDTO extends MyPageDTO {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "host")
    private String host;

    @Schema(description = "端口，备注：host + 端口，可以表示唯一标识")
    private Integer port;

    @Schema(description = "备注")
    private String remark;

}
