package com.cmcorg.engine.game.socket.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmcorg.engine.game.socket.server.model.entity.GameSocketServerDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface GameSocketServerMapper extends BaseMapper<GameSocketServerDO> {

    // 获取：每个：socketServer的连接数
    List<GameSocketServerDO> socketServerCurrentConnectTotalList(
        @Param("socketServerIdSet") Set<Long> socketServerIdSet);

}
