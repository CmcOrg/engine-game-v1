package com.cmcorg.engine.game.netty.tcp.protobuf.controller;

import com.cmcorg.engine.game.netty.tcp.protobuf.model.vo.NettyTcpProtoBufVO;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.netty.boot.annotation.NettyController;
import org.springframework.web.bind.annotation.RequestMapping;

@NettyController
public class NettyTcpProtoBufController {

    @RequestMapping(value = "/ping")
    public NettyTcpProtoBufVO ping() {
        return NettyTcpProtoBufVO.ok(BaseBizCodeEnum.OK);
    }

}
