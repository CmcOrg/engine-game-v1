package com.cmcorg.engine.game.netty.tcp.protobuf.properties;

import com.cmcorg.engine.web.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.SOCKET)
@RefreshScope
public class SocketProperties {

    @Schema(description = "最大连接数")
    private Integer maxConnect = 500;

}
