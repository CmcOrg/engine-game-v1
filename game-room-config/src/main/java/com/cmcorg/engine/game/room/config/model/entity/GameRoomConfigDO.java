package com.cmcorg.engine.game.room.config.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg.engine.game.room.config.model.enums.GameMoneyTypeEnum;
import com.cmcorg.engine.game.room.config.model.enums.GameRoomConfigPlayTypeEnum;
import com.cmcorg.engine.game.room.config.model.enums.GameRoomConfigRoomTypeEnum;
import com.cmcorg.engine.game.room.config.model.enums.GameUserExpTypeEnum;
import com.cmcorg.engine.web.auth.model.entity.BaseEntity;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestClass;
import com.cmcorg.engine.web.model.generate.model.annotation.RequestField;
import com.cmcorg.engine.web.model.generate.model.constant.WebModelConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@RequestClass(tableIgnoreFields = WebModelConstant.TABLE_IGNORE_FIELDS_TWO)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "主表：房间配置")
@TableName(value = "game_room_config")
@Data
public class GameRoomConfigDO extends BaseEntity {

    @Schema(description = "房间配置名称")
    private String name;

    @RequestField(formTitle = "排序号", hideInSearchFlag = true)
    @Schema(description = "排序号（值越大越前面，默认为 0）")
    private Integer orderNo;

    @Schema(description = "房间最大人数")
    private Integer maxUserTotal;

    @Schema(description = "房间最大数量")
    private Integer maxRoomTotal;

    @RequestField(tableTitle = "房间玩法")
    @Schema(description = "房间玩法：1 大厅（无法重连） 2 捕鱼 3 斗地主")
    private GameRoomConfigPlayTypeEnum playType;

    @RequestField(tableTitle = "房间类型")
    @Schema(description = "房间类型，例如：1000 普通大厅 2000 体验场 2001 普通场 2002 挑战场 2003 大奖赛")
    private GameRoomConfigRoomTypeEnum roomType;

    @RequestField(tableTitle = "消耗货币类型")
    @Schema(description = "消耗货币类型：1 金币 2 龙晶 3 钻石 4 临时货币（退房间清零）")
    private GameMoneyTypeEnum useMoneyType;

    @RequestField(tableTitle = "得到货币类型")
    @Schema(description = "得到货币类型：1 金币 2 龙晶 3 钻石 4 临时货币（退房间清零）")
    private GameMoneyTypeEnum gotMoneyType;

    @RequestField(tableTitle = "用户限制货币类型")
    @Schema(description = "用户限制货币类型：1 金币 2 龙晶 3 钻石 4 临时货币（退房间清零）")
    private GameMoneyTypeEnum limitMoneyType;

    @Schema(description = "用户携带货币最低值")
    private BigDecimal minUserMoney;

    @Schema(description = "用户携带货币最高值")
    private BigDecimal maxUserMoney;

    @RequestField(tableTitle = "用户限制经验值类型")
    @Schema(description = "用户限制经验值类型：1 普通经验")
    private GameUserExpTypeEnum limitExpType;

    @Schema(description = "最低用户经验值")
    private BigDecimal minUserExp;

    @Schema(description = "最高用户经验值")
    private BigDecimal maxUserExp;

    @RequestField(tableTitle = "房间增加经验值的类型")
    @Schema(description = "房间增加经验值的类型：1 普通经验")
    private GameUserExpTypeEnum roomExpType;

    @TableField(exist = false)
    @Schema(description = "连接数")
    private Long connectTotal;

    @TableField(exist = false)
    @Schema(description = "当前房间数")
    private Long roomCurrentTotal;

}
