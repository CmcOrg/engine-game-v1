package com.cmcorg.engine.game.netty.tcp.protobuf.server;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmcorg.engine.game.auth.configuration.GameJwtValidatorConfiguration;
import com.cmcorg.engine.game.netty.tcp.protobuf.configuration.IAcceptRoomTypeConfiguration;
import com.cmcorg.engine.game.room.current.model.bo.GameRoomCurrentJoinRoomRedisBO;
import com.cmcorg.engine.game.socket.server.model.enums.GameRedisKeyEnum;
import com.cmcorg.engine.web.auth.util.MyJwtUtil;
import com.cmcorg.engine.web.model.model.constant.BaseConstant;
import com.cmcorg.engine.web.model.model.constant.LogTopicConstant;
import com.cmcorg.engine.web.redisson.util.RedissonUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ChannelHandler.Sharable
@Slf4j(topic = LogTopicConstant.NETTY)
public class NettyTcpProtoBufServerHandler extends ChannelInboundHandlerAdapter {

    // 没有进行身份认证的通道，一般这种通道，业务可以忽略，备注：一定时间内，会关闭此类型通道
    private static final Map<String, Channel> NOT_SECURITY_CHANNEL_MAP = MapUtil.newConcurrentHashMap();
    // 通道，连接时间，时间戳
    private static final AttributeKey<Long> CREATE_TIME = AttributeKey.valueOf("createTime");
    // 移除规定时间内，没有身份认证成功的通道，中的【规定时间】
    public static final long SECURITY_EXPIRE_TIME = BaseConstant.SHORT_CODE_EXPIRE_TIME;

    // 进行了身份认证的通道，备注：一个【角色】，只能有一个通道，用户可以拥有多个通道
    private static final Map<Long, Channel> GAME_USER_ID_CHANNEL_MAP = MapUtil.newConcurrentHashMap();
    // userId key
    private static final AttributeKey<Long> USER_ID_KEY = AttributeKey.valueOf("userId");
    // gameUserId key
    private static final AttributeKey<Long> GAME_USER_ID_KEY =
        AttributeKey.valueOf(GameJwtValidatorConfiguration.PAYLOAD_MAP_GAME_USER_ID_KEY);
    // gameRoomCurrentJoinRoomRedisBO key
    public static final AttributeKey<GameRoomCurrentJoinRoomRedisBO> GAME_ROOM_CURRENT_JOIN_ROOM_REDIS_BO_KEY =
        AttributeKey.valueOf("GameRoomCurrentJoinRoomRedisBO");
    // 进行了身份认证通道的最后活跃时间，时间戳
    private static final AttributeKey<Long> ACTIVE_TIME = AttributeKey.valueOf("activeTime");
    // 移除规定时间内，没有进行发送任意消息的，进行了身份认证成功通道，中的【规定时间】
    public static final long HEARTBEAT_EXPIRE_TIME = BaseConstant.SECOND_30_EXPIRE_TIME;

    private static List<IAcceptRoomTypeConfiguration> iAcceptRoomTypeConfigurationList;

    public NettyTcpProtoBufServerHandler(
        @Autowired(required = false) List<IAcceptRoomTypeConfiguration> iAcceptRoomTypeConfigurationList) {
        NettyTcpProtoBufServerHandler.iAcceptRoomTypeConfigurationList = iAcceptRoomTypeConfigurationList;
    }

    /**
     * 获取：进行了身份认证的通道
     */
    protected static Map<Long, Channel> getGameUserIdChannelMap() {
        return GAME_USER_ID_CHANNEL_MAP;
    }

    /**
     * 定时：移除规定时间内，没有身份认证成功的通道
     * 备注：就算子类加了 @Component注解，本方法，规定时间内也只会被执行一次
     */
    @Scheduled(fixedRate = 10 * 1000)
    private void removeNotSecurityChannel() {
        for (Map.Entry<String, Channel> item : NOT_SECURITY_CHANNEL_MAP.entrySet()) {
            if ((System.currentTimeMillis() - item.getValue().attr(CREATE_TIME).get()) > SECURITY_EXPIRE_TIME) {
                item.getValue().close(); // 关闭通道
            }
        }
    }

    /**
     * 移除规定时间内，没有进行发送任意消息的，进行了身份认证成功通道
     */
    @Scheduled(fixedRate = 10 * 1000)
    private void removeNotHeartbeatChannel() {
        for (Map.Entry<Long, Channel> item : GAME_USER_ID_CHANNEL_MAP.entrySet()) {
            if ((System.currentTimeMillis() - item.getValue().attr(ACTIVE_TIME).get()) > HEARTBEAT_EXPIRE_TIME) {
                item.getValue().close(); // 关闭通道
            }
        }
    }

    /**
     * 连接成功时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ctx.channel().attr(CREATE_TIME).set(System.currentTimeMillis());

        NOT_SECURITY_CHANNEL_MAP.put(ctx.channel().id().asLongText(), ctx.channel());

        log.info("通道连接成功，通道 id：{}，当前没有进行身份认证的通道总数：{}", ctx.channel().id().asLongText(),
            NOT_SECURITY_CHANNEL_MAP.size());

        super.channelActive(ctx);

    }

    /**
     * 调用 close等操作，连接断开时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        Long gameUserId = ctx.channel().attr(GAME_USER_ID_KEY).get();

        if (gameUserId != null) {
            Channel channel = GAME_USER_ID_CHANNEL_MAP.get(gameUserId);
            if (channel != null && channel.id().asLongText().equals(ctx.channel().id().asLongText())) {
                GAME_USER_ID_CHANNEL_MAP.remove(gameUserId);
            }
        } else {
            NOT_SECURITY_CHANNEL_MAP.remove(ctx.channel().id().asLongText());
        }

        log.info("通道断开连接，游戏用户 id：{}，通道 id：{}，当前没有进行身份认证的通道总数：{}，当前进行了身份认证的通道总数：{}",
            ctx.channel().attr(GAME_USER_ID_KEY).get(), ctx.channel().id().asLongText(),
            NOT_SECURITY_CHANNEL_MAP.size(), GAME_USER_ID_CHANNEL_MAP.size());

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

    /**
     * 收到消息时
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        try {

            if (ctx.channel().attr(GAME_USER_ID_KEY).get() == null) {

                String channelIdStr = ctx.channel().id().asLongText();

                log.info("处理身份认证的消息，通道 id：{}", channelIdStr);

                // 处理：身份认证的消息，成功之后调用：consumer 即可
                NettyTcpProtoBufServerHandlerHelper
                    .handlerSecurityMessage(msg, ctx.channel(), (gameRoomCurrentJoinRoomRedisBO) -> {

                        Long gameUserId = gameRoomCurrentJoinRoomRedisBO.getGameUserId();

                        // 身份认证成功，之后的处理
                        RedissonUtil.doLock(GameRedisKeyEnum.PRE_SOCKET_AUTH_GAME_USER_ID.name() + gameUserId, () -> {

                            Channel channel = GAME_USER_ID_CHANNEL_MAP.get(gameUserId);

                            if (channel != null) {
                                channel.close(); // 移除之前的通道，备注：这里是异步的
                            }

                            ctx.channel().attr(USER_ID_KEY).set(gameRoomCurrentJoinRoomRedisBO.getUserId());

                            ctx.channel().attr(GAME_USER_ID_KEY).set(gameUserId);

                            ctx.channel().attr(GAME_ROOM_CURRENT_JOIN_ROOM_REDIS_BO_KEY)
                                .set(gameRoomCurrentJoinRoomRedisBO);

                            ctx.channel().attr(ACTIVE_TIME).set(System.currentTimeMillis());

                            GAME_USER_ID_CHANNEL_MAP.put(gameUserId, ctx.channel());

                            NOT_SECURITY_CHANNEL_MAP.remove(channelIdStr);

                            log.info("处理身份认证的消息成功，游戏用户 id：{}，通道 id：{}，当前没有进行身份认证的通道总数：{}，当前进行了身份认证的通道总数：{}",
                                ctx.channel().attr(GAME_USER_ID_KEY).get(), channelIdStr,
                                NOT_SECURITY_CHANNEL_MAP.size(), GAME_USER_ID_CHANNEL_MAP.size());

                            return null;

                        });

                        if (CollUtil.isNotEmpty(iAcceptRoomTypeConfigurationList)) {
                            for (IAcceptRoomTypeConfiguration item : iAcceptRoomTypeConfigurationList) {
                                item.handlerGameRoomCurrentJoinRoomRedisBO(ctx.channel()
                                    .attr(NettyTcpProtoBufServerHandler.GAME_ROOM_CURRENT_JOIN_ROOM_REDIS_BO_KEY)
                                    .get()); // 处理 gameRoomCurrentJoinRoomRedisBO
                            }
                        }

                    });

                return;
            }

            JSONObject principalJson =
                JSONUtil.createObj().set(MyJwtUtil.PAYLOAD_MAP_USER_ID_KEY, ctx.channel().attr(USER_ID_KEY).get())
                    .set(GameJwtValidatorConfiguration.PAYLOAD_MAP_GAME_USER_ID_KEY,
                        ctx.channel().attr(GAME_USER_ID_KEY).get());

            // 把 principalJson 设置到：security的上下文里面
            SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(principalJson, null, null));

            boolean illegalFlag = NettyTcpProtoBufServerHandlerHelper.handlerMessage(msg); // 处理：进行了身份认证的通道的消息

            if (BooleanUtil.isFalse(illegalFlag)) {
                ctx.channel().attr(ACTIVE_TIME).set(System.currentTimeMillis()); // 不是非法请求，才记录活跃时间
            }

        } catch (Throwable e) {
            NettyTcpProtoBufServerHandlerHelper.exceptionAdvice(e); // 处理业务异常
        } finally {
            ReferenceCountUtil.release(msg); // 释放资源
        }
    }

}
