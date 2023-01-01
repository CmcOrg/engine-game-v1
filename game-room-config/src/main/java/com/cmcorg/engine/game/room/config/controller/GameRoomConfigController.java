package com.cmcorg.engine.game.room.config.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg.engine.game.auth.model.entity.GameRoomConfigDO;
import com.cmcorg.engine.game.room.config.model.dto.GameRoomConfigInsertOrUpdateDTO;
import com.cmcorg.engine.game.room.config.model.dto.GameRoomConfigPageDTO;
import com.cmcorg.engine.game.room.config.service.GameRoomConfigService;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.model.generate.model.annotation.WebPage;
import com.cmcorg.engine.web.model.generate.model.enums.PageTypeEnum;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@WebPage(type = PageTypeEnum.ADMIN, title = "房间配置")
@RestController
@RequestMapping(value = "/game/roomConfig")
@Tag(name = "房间-配置")
public class GameRoomConfigController {

    @Resource
    GameRoomConfigService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('gameRoomConfig:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid GameRoomConfigInsertOrUpdateDTO dto) {
        return ApiResultVO.ok(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('gameRoomConfig:page')")
    public ApiResultVO<Page<GameRoomConfigDO>> myPage(@RequestBody @Valid GameRoomConfigPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('gameRoomConfig:infoById')")
    public ApiResultVO<GameRoomConfigDO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.infoById(notNullId));
    }

    @Operation(summary = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('gameRoomConfig:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @Operation(summary = "用户，分页排序查询")
    @PostMapping("/user/page")
    public ApiResultVO<Page<GameRoomConfigDO>> userPage() {
        return ApiResultVO.ok(baseService.userPage());
    }

}
