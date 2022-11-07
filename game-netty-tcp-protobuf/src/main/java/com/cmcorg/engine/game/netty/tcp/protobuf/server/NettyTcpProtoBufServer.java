package com.cmcorg.engine.game.netty.tcp.protobuf.server;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.cmcorg.engine.game.socker.server.model.entity.GameSocketServerDO;
import com.cmcorg.engine.game.socker.server.service.GameSocketServerService;
import com.cmcorg.engine.web.auth.configuration.BaseConfiguration;
import com.cmcorg.engine.web.auth.properties.CommonProperties;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.netty.boot.configuration.NettyBeanPostProcessor;
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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import protobuf.proto.BaseProto;

import javax.annotation.Resource;

@Component
@Slf4j
public class NettyTcpProtoBufServer implements CommandLineRunner, DisposableBean {

    @Resource
    NettyTcpProtoBufServerHandler nettyServerHandler;
    @Resource
    GameSocketServerService gameSocketServerService;
    @Resource
    CommonProperties commonProperties;

    private Long socketServerId = null; // 启动完成之后，这个属性才有值

    @Override
    public void run(String... args) {
        ThreadUtil.execute(this::start);
    }

    @SneakyThrows
    public void start() {

        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();

        int port = BaseConfiguration.port + 1;

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(parentGroup, childGroup) // 绑定线程池
                .channel(NioServerSocketChannel.class) // 指定使用的channel
                .localAddress(port) // 绑定监听端口
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

            insertForStartSocketServer(port); // 添加：启动数据到数据库

            log.info("NettyServer 启动完成，端口：{}，总接口个数：{}个", BaseConfiguration.port + 1,
                NettyBeanPostProcessor.getMappingMapSize());

            channelFuture.channel().closeFuture().sync(); // 阻塞线程，监听关闭事件
        } finally {
            parentGroup.shutdownGracefully(); // 释放线程池资源
            childGroup.shutdownGracefully();
        }
    }

    /**
     * 添加：启动数据到数据库
     */
    private void insertForStartSocketServer(int port) {

        GameSocketServerDO gameSocketServerDO = new GameSocketServerDO();
        gameSocketServerDO.setHost(commonProperties.getInternetAddress());
        gameSocketServerDO.setPort(port);
        gameSocketServerDO.setMaxConnect(500); // 最大连接数
        gameSocketServerDO.setEnableFlag(true);
        gameSocketServerDO.setDelFlag(false);
        gameSocketServerDO.setRemark("");
        gameSocketServerService.insertForStartSocketServer(gameSocketServerDO);

        socketServerId = gameSocketServerDO.getId();

    }

    /**
     * 服务关闭时
     */
    @Override
    public void destroy() {
        if (socketServerId != null) {
            log.info("删除 socketServer的数据库数据：socketServerId：{}", socketServerId);
            gameSocketServerService.deleteByIdSet(new NotEmptyIdSet(CollUtil.newHashSet(socketServerId)));
        }
    }

}
