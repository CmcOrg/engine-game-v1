package com.cmcorg.engine.game.room.config.model.dto;

import com.cmcorg.engine.game.room.config.model.enums.GameMoneyTypeEnum;
import com.cmcorg.engine.game.room.config.model.enums.GameRoomConfigPlayTypeEnum;
import com.cmcorg.engine.game.room.config.model.enums.GameRoomConfigRoomTypeEnum;
import com.cmcorg.engine.game.room.config.model.enums.GameUserExpTypeEnum;
import com.cmcorg.engine.web.auth.model.dto.MyPageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameRoomConfigPageDTO extends MyPageDTO {

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

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
