package com.cmcorg.engine.game.netty.tcp.protobuf.model.vo;

import cn.hutool.core.util.StrUtil;
import com.cmcorg.engine.game.netty.tcp.protobuf.exception.BaseException;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.model.exception.IBizCode;
import com.google.protobuf.ByteString;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class NettyTcpProtoBufVO {

    @Schema(description = "路径，备注：必须设置，不然会报错")
    private String uri;

    @Schema(description = "响应代码，成功返回：200")
    private Integer code;

    @Schema(description = "响应描述")
    private String msg;

    @Schema(description = "数据")
    private ByteString data;

    private NettyTcpProtoBufVO(String uri, Integer code, String msg, ByteString data) {
        setUri(uri);
        setMsg(msg);
        setCode(code);
        setData(data);
    }

    public NettyTcpProtoBufVO setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public NettyTcpProtoBufVO setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    private NettyTcpProtoBufVO end() {
        throw new BaseException(this);
    }

    /**
     * 系统异常
     */
    public static NettyTcpProtoBufVO sysError() {
        return error(BaseBizCodeEnum.API_RESULT_SYS_ERROR);
    }

    /**
     * 操作失败，备注：uri由框架自行填入，并且和客户端请求的 uri保持一致
     */
    public static NettyTcpProtoBufVO error(IBizCode iBizCode) {
        return new NettyTcpProtoBufVO(null, iBizCode.getCode(), iBizCode.getMsg(), null).end();
    }

    public static NettyTcpProtoBufVO error(IBizCode iBizCode, ByteString data) {
        return new NettyTcpProtoBufVO(null, iBizCode.getCode(), iBizCode.getMsg(), data).end();
    }

    public static NettyTcpProtoBufVO error(String msgTemp, Object... paramArr) {
        return new NettyTcpProtoBufVO(null, BaseBizCodeEnum.API_RESULT_SYS_ERROR.getCode(),
            StrUtil.format(msgTemp, paramArr), null).end();
    }

    /**
     * 操作成功，备注：uri由框架自行填入，并且和客户端请求的 uri保持一致
     */
    public static NettyTcpProtoBufVO ok(String msg, ByteString data) {
        return new NettyTcpProtoBufVO(null, BaseBizCodeEnum.API_RESULT_OK.getCode(), msg, data);
    }

    public static NettyTcpProtoBufVO ok(ByteString data) {
        return new NettyTcpProtoBufVO(null, BaseBizCodeEnum.API_RESULT_OK.getCode(),
            BaseBizCodeEnum.API_RESULT_OK.getMsg(), data);
    }

    public static NettyTcpProtoBufVO ok(String msg) {
        return new NettyTcpProtoBufVO(null, BaseBizCodeEnum.API_RESULT_OK.getCode(), msg, null);
    }

}
