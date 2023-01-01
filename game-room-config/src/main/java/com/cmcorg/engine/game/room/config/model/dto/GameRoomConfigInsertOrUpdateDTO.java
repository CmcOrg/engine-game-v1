package com.cmcorg.engine.game.room.config.model.dto;

import com.cmcorg.engine.game.auth.model.enums.GameMoneyTypeEnum;
import com.cmcorg.engine.game.auth.model.enums.GameRoomConfigPlayTypeEnum;
import com.cmcorg.engine.game.auth.model.enums.GameRoomConfigRoomTypeEnum;
import com.cmcorg.engine.game.auth.model.enums.GameUserExpTypeEnum;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestField;
import com.cmcorg.engine.web.model.model.dto.BaseInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class GameRoomConfigInsertOrUpdateDTO extends BaseInsertOrUpdateDTO {

    @Schema(description = "主键id")
    private Long id;

    @RequestField(formTitle = "配置名称")
    @Size(max = 10)
    @NotBlank
    @Schema(description = "房间配置名称")
    private String name;

    @RequestField(formTitle = "排序号", formTooltip = "值越大越前面")
    @Schema(description = "排序号（值越大越前面，默认为 0）")
    private Integer orderNo;

    @Schema(description = "房间最大人数")
    private Integer maxUserTotal;

    @Schema(description = "房间最大数量")
    private Integer maxRoomTotal;

    @RequestField(formTitle = "房间玩法", formTooltip = "1 大厅 2 捕鱼 3 斗地主")
    @NotNull
    @Schema(description = "房间玩法：1 大厅（无法重连） 2 捕鱼 3 斗地主")
    private GameRoomConfigPlayTypeEnum playType;

    @RequestField(formTitle = "房间类型", formTooltip = "1000 普通大厅 2000 体验场 2001 普通场 2002 挑战场 2003 大奖赛")
    @NotNull
    @Schema(description = "房间类型，例如：1000 普通大厅 2000 体验场 2001 普通场 2002 挑战场 2003 大奖赛")
    private GameRoomConfigRoomTypeEnum roomType;

    @RequestField(formTitle = "消耗货币类型", formTooltip = "1 金币 2 龙晶 3 钻石 4 临时货币（退房间清零）")
    @NotNull
    @Schema(description = "消耗货币类型：1 金币 2 龙晶 3 钻石 4 临时货币（退房间清零）")
    private GameMoneyTypeEnum useMoneyType;

    @RequestField(formTitle = "得到货币类型", formTooltip = "1 金币 2 龙晶 3 钻石 4 临时货币（退房间清零）")
    @NotNull
    @Schema(description = "得到货币类型：1 金币 2 龙晶 3 钻石 4 临时货币（退房间清零）")
    private GameMoneyTypeEnum gotMoneyType;

    @RequestField(formTitle = "用户限制货币类型", formTooltip = "1 金币 2 龙晶 3 钻石 4 临时货币（退房间清零）")
    @NotNull
    @Schema(description = "用户限制货币类型：1 金币 2 龙晶 3 钻石 4 临时货币（退房间清零）")
    private GameMoneyTypeEnum limitMoneyType;

    @Schema(description = "用户携带货币最低值")
    private BigDecimal minUserMoney;

    @Schema(description = "用户携带货币最高值")
    private BigDecimal maxUserMoney;

    @RequestField(formTitle = "用户限制经验值类型", formTooltip = "1 普通经验")
    @NotNull
    @Schema(description = "用户限制经验值类型：1 普通经验")
    private GameUserExpTypeEnum limitExpType;

    @Schema(description = "最低用户经验值")
    private BigDecimal minUserExp;

    @Schema(description = "最高用户经验值")
    private BigDecimal maxUserExp;

    @RequestField(formTitle = "房间增加经验值的类型", formTooltip = "1 普通经验")
    @NotNull
    @Schema(description = "房间增加经验值的类型：1 普通经验")
    private GameUserExpTypeEnum roomExpType;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

}
