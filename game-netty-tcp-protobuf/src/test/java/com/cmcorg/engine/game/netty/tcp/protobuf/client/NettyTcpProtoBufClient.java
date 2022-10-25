package com.cmcorg.engine.game.netty.tcp.protobuf.client;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.thread.GlobalThreadPool;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg.engine.game.netty.tcp.protobuf.model.enums.NettyOtherPathEnum;
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
import org.slf4j.LoggerFactory;
import protobuf.proto.BaseProto;
import protobuf.proto.ConnectProto;

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
    private static final int PORT = 20001;

    public static Channel channel = null;

    public static void main(String[] args) {

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

                    if (nextLine.startsWith("se ")) {

                        String code = StrUtil.subAfter(nextLine, "se ", false);

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
                        ch.pipeline().addLast(new NettyTcpProtoBufClientHandler());
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
