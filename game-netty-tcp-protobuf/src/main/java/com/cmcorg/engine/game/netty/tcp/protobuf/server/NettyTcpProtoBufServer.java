package com.cmcorg.engine.game.netty.tcp.protobuf.server;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.cmcorg.engine.game.auth.model.enums.GameRoomConfigRoomTypeEnum;
import com.cmcorg.engine.game.netty.tcp.protobuf.configuration.IAcceptRoomTypeConfiguration;
import com.cmcorg.engine.game.netty.tcp.protobuf.properties.SocketProperties;
import com.cmcorg.engine.game.socket.server.model.entity.GameSocketServerDO;
import com.cmcorg.engine.game.socket.server.service.GameSocketServerService;
import com.cmcorg.engine.web.auth.configuration.BaseConfiguration;
import com.cmcorg.engine.web.auth.properties.CommonProperties;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.netty.boot.configuration.NettyBeanPostProcessor;
import com.cmcorg.engine.web.util.util.SeparatorUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import protobuf.proto.BaseProto;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NettyTcpProtoBufServer implements CommandLineRunner, DisposableBean {

    @Resource
    NettyTcpProtoBufServerHandler nettyServerHandler;
    @Resource
    GameSocketServerService gameSocketServerService;
    @Resource
    CommonProperties commonProperties;
    @Resource
    SocketProperties socketProperties;
    @Autowired(required = false)
    List<IAcceptRoomTypeConfiguration> iAcceptRoomTypeConfigurationList;

    private Long socketServerId = null; // ??????????????????????????????????????????

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
            serverBootstrap.group(parentGroup, childGroup) // ???????????????
                .channel(NioServerSocketChannel.class) // ???????????????channel
                .localAddress(port) // ??????????????????
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(@NotNull SocketChannel ch) { // ???????????????????????????????????????
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        ch.pipeline().addLast(new ProtobufDecoder(BaseProto.BaseRequest.getDefaultInstance()));
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(nettyServerHandler);
                    }
                });

            ChannelFuture channelFuture = serverBootstrap.bind().sync(); // ???????????????

            insertForStartSocketServer(port); // ?????????????????????????????????

            log.info("NettyServer ????????????????????????{}?????????????????????{}???", BaseConfiguration.port + 1,
                NettyBeanPostProcessor.getMappingMapSize());

            channelFuture.channel().closeFuture().sync(); // ?????????????????????????????????

        } finally {
            parentGroup.shutdownGracefully(); // ?????????????????????
            childGroup.shutdownGracefully();
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void insertForStartSocketServer(int port) {

        // ????????????????????????
        Set<Integer> acceptRoomTypeCodeSet;

        if (CollUtil.isNotEmpty(iAcceptRoomTypeConfigurationList)) {
            acceptRoomTypeCodeSet =
                iAcceptRoomTypeConfigurationList.stream().flatMap(it -> it.acceptRoomTypeSet().stream())
                    .map(GameRoomConfigRoomTypeEnum::getCode).collect(Collectors.toSet());
        } else {
            acceptRoomTypeCodeSet = CollUtil.newHashSet();
        }

        GameSocketServerDO gameSocketServerDO = new GameSocketServerDO();
        gameSocketServerDO.setHost(commonProperties.getInternetAddress());
        gameSocketServerDO.setPort(port);
        gameSocketServerDO.setMaxConnect(socketProperties.getMaxConnect()); // ???????????????
        gameSocketServerDO
            .setAcceptRoomTypeCodeSeparatorStr(SeparatorUtil.verticalLine(acceptRoomTypeCodeSet)); // ?????????????????????
        gameSocketServerDO.setEnableFlag(true);
        gameSocketServerDO.setDelFlag(false);
        gameSocketServerDO.setRemark("");
        gameSocketServerService.insertForStartSocketServer(gameSocketServerDO);

        socketServerId = gameSocketServerDO.getId();

    }

    /**
     * ???????????????
     */
    @Override
    public void destroy() {
        if (socketServerId != null) {
            log.info("?????? socketServer?????????????????????socketServerId???{}", socketServerId);
            gameSocketServerService.deleteByIdSet(new NotEmptyIdSet(CollUtil.newHashSet(socketServerId)));
        }
    }

}
