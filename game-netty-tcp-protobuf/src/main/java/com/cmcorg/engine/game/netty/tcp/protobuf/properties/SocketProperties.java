package com.cmcorg.engine.game.netty.tcp.protobuf.properties;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg.engine.web.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.SOCKET)
@RefreshScope
public class SocketProperties {

    @Schema(description = "最大连接数")
    private Integer maxConnect = 500;

    @Schema(description = "支持的，房间类型 codeSet")
    private Set<Integer> acceptRoomTypeCodeSet = CollUtil.newHashSet();

}
