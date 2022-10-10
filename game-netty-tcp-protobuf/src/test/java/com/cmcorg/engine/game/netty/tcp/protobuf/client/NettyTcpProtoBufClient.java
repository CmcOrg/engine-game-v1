package com.cmcorg.engine.game.netty.tcp.protobuf.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.thread.GlobalThreadPool;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg.engine.game.netty.tcp.protobuf.model.enums.NettyOtherPathEnum;
import com.cmcorg.engine.game.netty.tcp.protobuf.proto.BaseProto;
import com.cmcorg.engine.game.netty.tcp.protobuf.proto.ConnectProto;
import com.cmcorg.engine.game.netty.tcp.protobuf.server.NettyTcpProtoBufServerHandler;
import com.cmcorg.engine.web.redisson.enums.RedisKeyEnum;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

@Slf4j
public class NettyTcpProtoBufClient {

    static {
        // 设置为：info日志级别
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        List<Logger> loggerList = loggerContext.getLoggerList();
        loggerList.forEach(logger -> logger.setLevel(Level.INFO));
    }

    private static final String IP = "127.0.0.1";
    private static final int PORT = 11000;

    private static final String NODE_ADDRESS_STR =
        "192.168.56.10:6001,192.168.56.10:6002,192.168.56.10:6003,192.168.56.10:6004,192.168.56.10:6005,192.168.56.10:6006";

    private static final String PASSWORD = "123456";

    public static Channel channel = null;

    public static void main(String[] args) {

        String[] nodeAddressArr =
            StrUtil.splitTrim(NODE_ADDRESS_STR, ",").stream().map(it -> StrUtil.addPrefixIfNot(it, "redis://"))
                .toArray(String[]::new);

        Config config = new Config();
        config.useClusterServers().addNodeAddress(nodeAddressArr).setPassword(PASSWORD)
            .setMasterConnectionMinimumIdleSize(2).setSlaveConnectionMinimumIdleSize(2);
        config.setCodec(new JsonJacksonCodec()); // 设置为：json序列化，目的：方便看

        RedissonClient redissonClient = Redisson.create(config);

        ThreadUtil.execute(NettyTcpProtoBufClient::start);  // 启动客户端

        try (Scanner scanner = new Scanner(System.in)) {

            while (true) {

                if (scanner.hasNextLine()) {

                    String nextLine = scanner.nextLine();

                    if (StrUtil.isBlank(nextLine)) {
                        continue;
                    }

                    if ("exit".equals(nextLine)) {
                        break;
                    }

                    if (channel == null) {
                        log.info("未连接上服务器，请稍等片刻");
                        continue;
                    }

                    if ("close".equals(nextLine)) { // 备注：效果类似 disconnect
                        channel.close();
                        continue;
                    }

                    if ("se".equals(nextLine)) {
                        String code = IdUtil.simpleUUID();
                        redissonClient.getBucket(RedisKeyEnum.PRE_NETTY_TCP_PROTO_BUF_CONNECT_SECURITY_CODE + code)
                            .set(0L); // 0：admin账号
                        log.info("发送身份认证消息：{}", code);
                        channel.writeAndFlush(
                            BaseProto.BaseRequest.newBuilder().setUri(NettyOtherPathEnum.CONNECT_SECURITY.getUri())
                                .setBody(ConnectProto.SecurityRequest.newBuilder().setCode(code).build().toByteString())
                                .build());
                        continue;
                    }

                    log.info("发送消息：{}", nextLine);
                    channel.writeAndFlush(BaseProto.BaseRequest.newBuilder().setUri(nextLine).build());

                }

            }

        } finally {
            GlobalThreadPool.shutdown(true);
            log.info("主线程关闭，请重启客户端");
        }

    }

    /**
     * 启动客户端
     */
    @SneakyThrows
    private static void start() {

        NettyTcpProtoBufClient.channel = null;

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap().group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        ch.pipeline().addLast(new ProtobufDecoder(BaseProto.BaseResponse.getDefaultInstance()));
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(new NettyTcpProtoBufServerHandler());
                    }
                });
            ChannelFuture future = bootstrap.connect(IP, PORT).sync();
            log.info("NettyClient 启动完成，连接：{}:{}", IP, PORT);
            future.channel().closeFuture().sync();
        } finally {
            // 备注：只要客户端断开连接，就会进入这里
            eventLoopGroup.shutdownGracefully(); // 释放资源

            log.info("客户端重连中...");
            ThreadUtil.sleep(2000);
            ThreadUtil.execute(NettyTcpProtoBufClient::start);  // 启动客户端
        }

    }

}
