package com.cmcorg.engine.game.socket.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg.engine.web.auth.model.entity.BaseEntity;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestClass;
import com.cmcorg.engine.web.model.generate.model.constant.WebModelConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@RequestClass(tableIgnoreFields = WebModelConstant.TABLE_IGNORE_FIELDS_TWO)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "game_socket_server")
@Data
@Schema(description = "主表：socket服务器")
public class GameSocketServerDO extends BaseEntity {

    @Schema(description = "host")
    private String host;

    @Schema(description = "端口，备注：host + 端口，可以表示唯一标识")
    private Integer port;

    @Schema(description = "最大连接数")
    private Integer maxConnect;

    @Schema(description = "支持的，房间类型 code 分隔符连接字符串")
    private String acceptRoomTypeCodeSeparatorStr;

    @TableField(exist = false)
    @Schema(description = "当前：连接数")
    private Long socketServerCurrentConnectTotal;

}
