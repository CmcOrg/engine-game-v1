package com.cmcorg.engine.game.room.current.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg.engine.game.auth.model.entity.GameRoomCurrentDO;
import com.cmcorg.engine.game.room.current.model.dto.GameRoomCurrentJoinRoomDTO;
import com.cmcorg.engine.game.room.current.model.dto.GameRoomCurrentPageDTO;
import com.cmcorg.engine.game.room.current.model.vo.GameRoomCurrentJoinRoomVO;
import com.cmcorg.engine.game.room.current.model.vo.GameRoomCurrentPageVO;
import com.cmcorg.engine.game.room.current.service.GameRoomCurrentService;
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

@WebPage(type = PageTypeEnum.ADMIN, title = "当前房间")
@RestController
@RequestMapping(value = "/game/roomCurrent")
@Tag(name = "房间-当前")
public class GameRoomCurrentController {

    @Resource
    GameRoomCurrentService baseService;

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('gameRoomCurrent:page')")
    public ApiResultVO<Page<GameRoomCurrentPageVO>> myPage(@RequestBody @Valid GameRoomCurrentPageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('gameRoomCurrent:infoById')")
    public ApiResultVO<GameRoomCurrentDO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.infoById(notNullId));
    }

    @Operation(summary = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('gameRoomCurrent:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @Operation(summary = "加入房间")
    @PostMapping("/joinRoom")
    public ApiResultVO<GameRoomCurrentJoinRoomVO> joinRoom(@RequestBody @Valid GameRoomCurrentJoinRoomDTO dto) {
        return ApiResultVO.ok(baseService.joinRoom(dto));
    }

}
