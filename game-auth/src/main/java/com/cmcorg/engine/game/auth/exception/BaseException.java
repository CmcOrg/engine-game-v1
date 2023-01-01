package com.cmcorg.engine.game.auth.exception;

import cn.hutool.json.JSONUtil;
import com.cmcorg.engine.game.auth.model.vo.NettyTcpProtoBufVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException {

    private NettyTcpProtoBufVO nettyTcpProtoBufVO;

    public BaseException(NettyTcpProtoBufVO nettyTcpProtoBufVO) {
        super(JSONUtil.toJsonStr(nettyTcpProtoBufVO)); // 把信息封装成json格式
        setNettyTcpProtoBufVO(nettyTcpProtoBufVO);
    }

}
