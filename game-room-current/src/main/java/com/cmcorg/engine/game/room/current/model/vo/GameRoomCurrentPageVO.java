package com.cmcorg.engine.game.room.current.model.vo;

import com.cmcorg.engine.game.auth.model.enums.GameMoneyTypeEnum;
import com.cmcorg.engine.game.auth.model.enums.GameRoomConfigPlayTypeEnum;
import com.cmcorg.engine.game.auth.model.enums.GameRoomConfigRoomTypeEnum;
import com.cmcorg.engine.game.auth.model.enums.GameUserExpTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class GameRoomCurrentPageVO {

    @Schema(description = "主键 id")
    private Long id;

    @Schema(description = "房间配置主键 id")
    private Long roomConfigId;

    @Schema(description = "房间配置名称")
    private String name;

    @Schema(description = "房间玩法：1 大厅（无法重连） 2 捕鱼 3 斗地主")
    private GameRoomConfigPlayTypeEnum playType;

    @Schema(description = "房间类型，例如：1000 普通大厅 2000 体验场 2001 普通场 2002 挑战场 2003 大奖赛")
    private GameRoomConfigRoomTypeEnum roomType;

    @Schema(description = "消耗货币类型：1 金币 2 龙晶 3 钻石 4 临时货币（退房间清零）")
    private GameMoneyTypeEnum useMoneyType;

    @Schema(description = "得到货币类型：1 金币 2 龙晶 3 钻石 4 临时货币（退房间清零）")
    private GameMoneyTypeEnum gotMoneyType;

    @Schema(description = "用户限制货币类型：1 金币 2 龙晶 3 钻石 4 临时货币（退房间清零）")
    private GameMoneyTypeEnum limitMoneyType;

    @Schema(description = "用户限制经验值类型：1 普通经验")
    private GameUserExpTypeEnum limitExpType;

    @Schema(description = "房间增加经验值的类型：1 普通经验")
    private GameUserExpTypeEnum roomExpType;

    @Schema(description = "socket服务器主键 id")
    private Long socketServerId;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "当前连接数")
    private Long roomCurrentConnectTotal;

}
