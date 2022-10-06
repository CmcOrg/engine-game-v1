package com.cmcorg.engine.game.netty.tcp.protobuf.server;

import cn.hutool.core.thread.ThreadUtil;
import com.cmcorg.engine.game.netty.tcp.protobuf.proto.BaseProto;
import com.cmcorg.engine.web.auth.configuration.BaseConfiguration;
import com.cmcorg.engine.web.netty.boot.configuration.NettyBeanPostProcessor;
import com.cmcorg.engine.web.netty.boot.handler.AbstractNettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class NettyTcpProtoBufServer implements CommandLineRunner {

    @Resource
    AbstractNettyServerHandler nettyServerHandler;

    @Override
    public void run(String... args) {
        ThreadUtil.execute(this::start);
    }

    @SneakyThrows
    public void start() {

        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(parentGroup, childGroup) // 绑定线程池
                .channel(NioServerSocketChannel.class) // 指定使用的channel
                .localAddress(BaseConfiguration.port + 1) // 绑定监听端口
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(@NotNull SocketChannel ch) { // 绑定客户端连接时候触发操作
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        ch.pipeline().addLast(new ProtobufDecoder(BaseProto.BaseRequest.getDefaultInstance()));
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(nettyServerHandler);
                    }
                });
            ChannelFuture channelFuture = serverBootstrap.bind().sync(); // 服务器创建
            log.info("NettyServer 启动完成，端口：{}，总接口个数：{}个", BaseConfiguration.port + 1,
                NettyBeanPostProcessor.getMappingMapSize());
            channelFuture.channel().closeFuture().sync(); // 阻塞线程，监听关闭事件
        } finally {
            parentGroup.shutdownGracefully(); // 释放线程池资源
            childGroup.shutdownGracefully();
        }
    }

}
