package com.cmcorg.engine.game.netty.tcp.protobuf.server;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg.engine.game.netty.tcp.protobuf.exception.BaseException;
import com.cmcorg.engine.game.netty.tcp.protobuf.model.enums.NettyOtherPathEnum;
import com.cmcorg.engine.game.netty.tcp.protobuf.model.vo.NettyTcpProtoBufVO;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.util.AuthUserUtil;
import com.cmcorg.engine.web.model.model.constant.LogTopicConstant;
import com.cmcorg.engine.web.netty.boot.configuration.NettyBeanPostProcessor;
import com.cmcorg.engine.web.netty.boot.exception.BizCodeEnum;
import com.cmcorg.engine.web.netty.boot.handler.AbstractNettyServerHandler;
import com.cmcorg.engine.web.redisson.enums.RedisKeyEnum;
import com.google.protobuf.ByteString;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import protobuf.proto.BaseProto;
import protobuf.proto.ConnectProto;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;
import java.util.function.Consumer;

@Component
@Slf4j(topic = LogTopicConstant.NETTY)
public class NettyTcpProtoBufServerHandler extends AbstractNettyServerHandler {

    @Resource
    RedissonClient redissonClient;

    /**
     * 处理：身份认证的消息
     */
    @Override
    public void handlerSecurityMessage(Object msg, Channel channel, Consumer<Long> consumer) {

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

            RBucket<Long> bucket = redissonClient
                .getBucket(RedisKeyEnum.PRE_NETTY_TCP_PROTO_BUF_CONNECT_SECURITY_CODE + securityRequest.getCode());

            Long userId = bucket.get();

            if (userId == null) {
                throw new RuntimeException(); // 备注：会被下面捕捉该异常
            }

            consumer.accept(userId); // 执行：回调

            // 响应：身份认证成功
            sendToChannel(NettyTcpProtoBufVO.ok(BaseBizCodeEnum.OK).setUri(baseRequest.getUri()), channel);

            bucket.delete(); // 移除：验证码

        } catch (Exception e) {
            e.printStackTrace();
            try {
                NettyTcpProtoBufVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
            } catch (BaseException baseException) {
                handlerAndSendBaseException(
                    BaseProto.BaseRequest.newBuilder().setUri(NettyOtherPathEnum.CONNECT_SECURITY.getUri()).build(),
                    baseException, channel);
            }
        }

    }

    /**
     * 处理：进行了身份认证的通道的消息
     */
    @Override
    public void handlerMessage(Object msg) {

        if (!(msg instanceof BaseProto.BaseRequest)) {
            try {
                NettyTcpProtoBufVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
            } catch (BaseException baseException) {
                handlerAndSendBaseException(
                    BaseProto.BaseRequest.newBuilder().setUri(NettyOtherPathEnum.COMMON_ERROR.getUri()).build(),
                    baseException, null);
                return;
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
                handlerAndSendBaseException(baseRequest, baseException, null);
                return;
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

        log.info("处理用户消息，用户 id：{}，uri：{}，body：{}", AuthUserUtil.getCurrentUserId(), baseRequest.getUri(),
            baseRequest.getBody());

        try {

            // 执行方法，备注：方法必须返回【NettyTcpProtoBufVO】类型
            Object invoke = ReflectUtil.invoke(mappingValue.getBean(), mappingValue.getMethod(), args);

            // 发送：返回值
            sendToSelf(((NettyTcpProtoBufVO)invoke).setUri(baseRequest.getUri()));

        } catch (Throwable e) {
            log.info("处理业务异常");
            e.printStackTrace();
            if (e instanceof BaseException) {
                handlerAndSendBaseException(baseRequest, (BaseException)e, null);
            } else {
                sendToSelf(NettyTcpProtoBufVO.sysError().setUri(baseRequest.getUri()));
            }
        }
    }

    /**
     * 处理并发送：BaseException，注意：这个方法不开放给其他类使用
     */
    private void handlerAndSendBaseException(BaseProto.BaseRequest baseRequest, BaseException baseException,
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
    @Override
    public void exceptionAdvice(Throwable e) {
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
        log.info("发送消息：sendToSelf，目标用户 id：{}，消息：{}", AuthUserUtil.getCurrentUserId(),
            JSONUtil.toJsonStr(nettyTcpProtoBufVO));
        doSend(getUserIdChannelMap().get(AuthUserUtil.getCurrentUserId()),
            handlerNettyTcpProtoBufVOToSend(nettyTcpProtoBufVO));
    }

    /**
     * 给 userIdSet发送消息
     */
    public static void sendByUserIdSet(Set<Long> userIdSet, NettyTcpProtoBufVO nettyTcpProtoBufVO) {
        log.info("发送消息：sendByUserIdSet，userIdSet：{}，消息：{}", userIdSet, JSONUtil.toJsonStr(nettyTcpProtoBufVO));
        BaseProto.BaseResponse baseResponse = handlerNettyTcpProtoBufVOToSend(nettyTcpProtoBufVO);
        for (Long item : userIdSet) {
            doSend(getUserIdChannelMap().get(item), baseResponse);
        }
    }

    /**
     * 给 所有人发送消息
     */
    public static void sendToAll(NettyTcpProtoBufVO nettyTcpProtoBufVO) {
        log.info("发送消息：sendToAll，总数：{}，消息：{}", getUserIdChannelMap().size(), JSONUtil.toJsonStr(nettyTcpProtoBufVO));
        BaseProto.BaseResponse baseResponse = handlerNettyTcpProtoBufVOToSend(nettyTcpProtoBufVO);
        for (Channel item : getUserIdChannelMap().values()) {
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
        }

        return builder.build();
    }

}
