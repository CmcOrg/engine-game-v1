package com.cmcorg.engine.game.netty.tcp.protobuf.server;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg.engine.game.auth.exception.BaseException;
import com.cmcorg.engine.game.auth.model.bo.GameRoomCurrentJoinRoomRedisBO;
import com.cmcorg.engine.game.auth.model.vo.NettyTcpProtoBufVO;
import com.cmcorg.engine.game.auth.util.GameAuthUserUtil;
import com.cmcorg.engine.game.netty.tcp.protobuf.model.enums.NettyOtherPathEnum;
import com.cmcorg.engine.game.socket.server.model.enums.GameRedisKeyEnum;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.model.model.constant.LogTopicConstant;
import com.cmcorg.engine.web.netty.boot.configuration.NettyBeanPostProcessor;
import com.cmcorg.engine.web.netty.boot.exception.BizCodeEnum;
import com.google.protobuf.ByteString;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import protobuf.proto.BaseProto;
import protobuf.proto.ConnectProto;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;
import java.util.function.Consumer;

@Component
@Slf4j(topic = LogTopicConstant.NETTY)
public class NettyTcpProtoBufServerHandlerHelper {

    // 当处理消息的时间超过这个值时，会进行警告，单位：毫秒
    private static final int WARING_HANDLER_MESSAGE_MS = 100;

    // 当要发送的消息长度超过这个值时，会进行警告，单位：byte
    private static final int WARING_SEND_MESSAGE_DATA_SIZE = 10 * 10000;

    private static RedissonClient redissonClient;

    public NettyTcpProtoBufServerHandlerHelper(RedissonClient redissonClient) {
        NettyTcpProtoBufServerHandlerHelper.redissonClient = redissonClient;
    }

    /**
     * 处理：身份认证的消息
     */
    public static void handlerSecurityMessage(Object msg, Channel channel,
        Consumer<GameRoomCurrentJoinRoomRedisBO> consumer) {

        try {

            if (!(msg instanceof BaseProto.BaseRequest)) {
                throw new RuntimeException(); // 备注：会被下面捕捉该异常
            }

            BaseProto.BaseRequest baseRequest = (BaseProto.BaseRequest)msg;

            if (!NettyOtherPathEnum.CONNECT_SECURITY.getUri().equals(baseRequest.getUri())) {
                throw new RuntimeException(); // 备注：会被下面捕捉该异常
            }

            ConnectProto.SecurityRequest securityRequest =
                ConnectProto.SecurityRequest.parseFrom(baseRequest.getBody());

            RBucket<GameRoomCurrentJoinRoomRedisBO> bucket = redissonClient
                .getBucket(GameRedisKeyEnum.PRE_NETTY_TCP_PROTO_BUF_CONNECT_SECURITY_CODE + securityRequest.getCode());

            GameRoomCurrentJoinRoomRedisBO gameRoomCurrentJoinRoomRedisBO = bucket.get();

            if (gameRoomCurrentJoinRoomRedisBO == null) {
                throw new RuntimeException(); // 备注：会被下面捕捉该异常
            }

            consumer.accept(gameRoomCurrentJoinRoomRedisBO); // 执行：回调

            // 响应：身份认证成功
            sendToChannel(NettyTcpProtoBufVO.ok(BaseBizCodeEnum.OK_ENGLISH).setUri(baseRequest.getUri()), channel);

            bucket.delete(); // 移除：验证码

        } catch (Exception e) {
            e.printStackTrace();
            try {
                NettyTcpProtoBufVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
            } catch (BaseException baseException) {
                // 处理并发送：BaseException
                handlerAndSendBaseException(
                    BaseProto.BaseRequest.newBuilder().setUri(NettyOtherPathEnum.CONNECT_SECURITY.getUri()).build(),
                    baseException, channel);
            }
        }

    }

    /**
     * 处理：进行了身份认证的通道的消息
     * 返回：是否是非法访问：true 是 false 否
     */
    public static boolean handlerMessage(Object msg) {

        long l1 = System.currentTimeMillis();

        if (!(msg instanceof BaseProto.BaseRequest)) {
            try {
                NettyTcpProtoBufVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
            } catch (BaseException baseException) {
                // 处理并发送：BaseException
                handlerAndSendBaseException(
                    BaseProto.BaseRequest.newBuilder().setUri(NettyOtherPathEnum.COMMON_ERROR.getUri()).build(),
                    baseException, null);
                return true;
            }
        }

        BaseProto.BaseRequest baseRequest = (BaseProto.BaseRequest)msg;

        // 获取：uri映射的方法
        NettyBeanPostProcessor.MappingValue mappingValue =
            NettyBeanPostProcessor.getMappingValueByKey(baseRequest.getUri());

        if (mappingValue == null) {
            try {
                NettyTcpProtoBufVO.error(BizCodeEnum.PATH_NOT_FOUND.getMsg(), baseRequest.getUri());
            } catch (BaseException baseException) {
                handlerAndSendBaseException(baseRequest, baseException, null); // 处理并发送：BaseException
                return true;
            }
        }

        Parameter[] parameterArr = mappingValue.getMethod().getParameters();

        Object[] args = null;
        if (ArrayUtil.isNotEmpty(parameterArr)) {
            Parameter parameter = parameterArr[0]; // 获取：方法的第一个参数
            Method parseFromMethod = ClassUtil.getPublicMethod(parameter.getType(), "parseFrom", ByteString.class);
            if (parseFromMethod != null) {
                args = new Object[] {ReflectUtil.invokeStatic(parseFromMethod, baseRequest.getBody())}; // 执行：反序列化
            }
        }

        try {

            // 执行方法，备注：方法必须返回【NettyTcpProtoBufVO】类型
            Object invoke = ReflectUtil.invoke(mappingValue.getBean(), mappingValue.getMethod(), args);

            // 发送：返回值
            sendToSelf(((NettyTcpProtoBufVO)invoke).setUri(baseRequest.getUri()));

            long l2 = System.currentTimeMillis();

            if (l2 - l1 >= WARING_HANDLER_MESSAGE_MS) {
                log.info("处理用户消息时间过长，游戏用户 id：{}，uri：{}，bodySize：{}", GameAuthUserUtil.getCurrentGameUserId(),
                    baseRequest.getUri(), baseRequest.getBody().size());
                //            } else {
                //                log.info("处理耗时：{}", l2 - l1);
            }

        } catch (Throwable e) {
            log.info("处理业务异常");
            e.printStackTrace();
            if (e instanceof BaseException) {
                handlerAndSendBaseException(baseRequest, (BaseException)e, null); // 处理并发送：BaseException
            } else {
                sendToSelf(NettyTcpProtoBufVO.sysError().setUri(baseRequest.getUri()));
            }
        }

        return false;
    }

    /**
     * 处理并发送：BaseException，注意：这个方法不开放给其他类使用
     */
    private static void handlerAndSendBaseException(BaseProto.BaseRequest baseRequest, BaseException baseException,
        Channel channel) {
        if (channel == null) {
            sendToSelf(baseException.getNettyTcpProtoBufVO().setUri(baseRequest.getUri()));
        } else {
            sendToChannel(baseException.getNettyTcpProtoBufVO().setUri(baseRequest.getUri()), channel);
        }
    }

    /**
     * 处理业务异常
     */
    public static void exceptionAdvice(Throwable e) {
        e.printStackTrace(); // 只打印错误信息，因为：handlerMessage 方法已经处理了
    }

    /**
     * 给通道发送消息，注意：这个方法不开放给其他类使用
     */
    private static void sendToChannel(NettyTcpProtoBufVO nettyTcpProtoBufVO, Channel channel) {
        log.info("发送消息：sendToChannel，通道 id：{}，消息：{}", channel.id().asLongText(),
            JSONUtil.toJsonStr(nettyTcpProtoBufVO));
        doSend(channel, handlerNettyTcpProtoBufVOToSend(nettyTcpProtoBufVO));
    }

    /**
     * 给自己发送消息，备注：必须进行了身份认证的通道才行，不然无法发送
     */
    public static void sendToSelf(NettyTcpProtoBufVO nettyTcpProtoBufVO) {
        log.info("发送消息：sendToSelf，目标游戏用户 id：{}，消息：{}", GameAuthUserUtil.getCurrentGameUserId(),
            JSONUtil.toJsonStr(nettyTcpProtoBufVO));
        doSend(NettyTcpProtoBufServerHandler.getGameUserIdChannelMap().get(GameAuthUserUtil.getCurrentGameUserId()),
            handlerNettyTcpProtoBufVOToSend(nettyTcpProtoBufVO));
    }

    /**
     * 给 gameUserIdSet发送消息
     */
    public static void sendByGameUserIdSet(Set<Long> gameUserIdSet, NettyTcpProtoBufVO nettyTcpProtoBufVO) {
        log.info("发送消息：sendByGameUserIdSet，gameUserIdSet：{}，消息：{}", gameUserIdSet,
            JSONUtil.toJsonStr(nettyTcpProtoBufVO));
        BaseProto.BaseResponse baseResponse = handlerNettyTcpProtoBufVOToSend(nettyTcpProtoBufVO);
        for (Long item : gameUserIdSet) {
            doSend(NettyTcpProtoBufServerHandler.getGameUserIdChannelMap().get(item), baseResponse);
        }
    }

    /**
     * 给 所有人发送消息
     */
    public static void sendToAll(NettyTcpProtoBufVO nettyTcpProtoBufVO) {
        log.info("发送消息：sendToAll，总数：{}，消息：{}", NettyTcpProtoBufServerHandler.getGameUserIdChannelMap().size(),
            JSONUtil.toJsonStr(nettyTcpProtoBufVO));
        BaseProto.BaseResponse baseResponse = handlerNettyTcpProtoBufVOToSend(nettyTcpProtoBufVO);
        for (Channel item : NettyTcpProtoBufServerHandler.getGameUserIdChannelMap().values()) {
            doSend(item, baseResponse);
        }
    }

    /**
     * 执行：消息的发送
     */
    private static void doSend(Channel channel, BaseProto.BaseResponse baseResponse) {
        if (channel != null) {
            channel.writeAndFlush(baseResponse);
        }
    }

    /**
     * 统一处理：NettyTcpProtoBufVO，然后发送消息
     */
    private static BaseProto.BaseResponse handlerNettyTcpProtoBufVOToSend(NettyTcpProtoBufVO nettyTcpProtoBufVO) {

        BaseProto.BaseResponse.Builder builder =
            BaseProto.BaseResponse.newBuilder().setCode(nettyTcpProtoBufVO.getCode())
                .setMsg(nettyTcpProtoBufVO.getMsg()).setUri(nettyTcpProtoBufVO.getUri()); // 备注：这里 uri为 null则会报错

        if (nettyTcpProtoBufVO.getData() != null) {
            builder.setData(nettyTcpProtoBufVO.getData());
            if (nettyTcpProtoBufVO.getData().size() >= WARING_SEND_MESSAGE_DATA_SIZE) {
                log.info("发送消息 data过长：uri：{}，dataSize：{}，消息：{}", nettyTcpProtoBufVO.getUri(),
                    nettyTcpProtoBufVO.getData().size(), JSONUtil.toJsonStr(nettyTcpProtoBufVO));
            }
        }

        return builder.build();
    }

}
