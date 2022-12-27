package com.cmcorg.engine.game.netty.tcp.protobuf.model.bo;

import com.cmcorg.engine.game.room.config.model.entity.GameRoomConfigDO;
import com.cmcorg.engine.game.room.current.model.entity.GameRoomCurrentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Objects;

@Data
public class BasePlayerBO {

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "游戏用户主键 id")
    private Long gameUserId;

    @Schema(description = "用户当前房间的信息")
    private GameRoomCurrentDO gameRoomCurrentDO;

    @Schema(description = "用户当前房间的 配置信息")
    private GameRoomConfigDO gameRoomConfigDO;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasePlayerBO that = (BasePlayerBO)o;
        return getGameUserId().equals(that.getGameUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGameUserId());
    }

}
