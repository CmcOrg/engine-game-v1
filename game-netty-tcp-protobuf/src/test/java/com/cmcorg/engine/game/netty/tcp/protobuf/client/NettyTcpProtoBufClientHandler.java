package com.cmcorg.engine.game.netty.tcp.protobuf.client;

import com.cmcorg.engine.game.netty.tcp.protobuf.proto.BaseProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class NettyTcpProtoBufClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端连接成功");
        NettyTcpProtoBufClient.channel = ctx.channel();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端断开连接");
        NettyTcpProtoBufClient.channel = null;
        super.channelInactive(ctx);
    }

    /**
     * 发生异常时，比如：远程主机强迫关闭了一个现有的连接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        super.exceptionCaught(ctx, e);
        ctx.close(); // 会执行：channelInactive 方法
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof BaseProto.BaseResponse)) {
            log.info("错误：消息不是【BaseResponse】类型");
            return;
        }

        BaseProto.BaseResponse baseResponse = (BaseProto.BaseResponse)msg;

        log.info("客户端收到的消息：uri：{}，code：{}，msg：{}，data：{}", baseResponse.getUri(), baseResponse.getCode(),
            baseResponse.getMsg(), baseResponse.getData());

    }
}
