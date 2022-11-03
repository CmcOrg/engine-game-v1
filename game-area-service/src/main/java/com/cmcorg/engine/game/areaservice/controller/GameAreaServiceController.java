package com.cmcorg.engine.game.areaservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg.engine.game.areaservice.model.dto.*;
import com.cmcorg.engine.game.areaservice.model.entity.GameAreaServiceDO;
import com.cmcorg.engine.game.areaservice.service.GameAreaServiceService;
import com.cmcorg.engine.game.user.model.entity.GameUserDO;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
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

@WebPage(type = PageTypeEnum.ADMIN, title = "区服管理")
@RestController
@RequestMapping(value = "/game/areaService")
@Tag(name = "区服-管理")
public class GameAreaServiceController {

    @Resource
    GameAreaServiceService baseService;

    @Operation(summary = "新增/修改")
    @PostMapping("/insertOrUpdate")
    @PreAuthorize("hasAuthority('gameAreaService:insertOrUpdate')")
    public ApiResultVO<String> insertOrUpdate(@RequestBody @Valid GameAreaServiceInsertOrUpdateDTO dto) {
        return ApiResultVO.ok(baseService.insertOrUpdate(dto));
    }

    @Operation(summary = "分页排序查询")
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('gameAreaService:page')")
    public ApiResultVO<Page<GameAreaServiceDO>> myPage(@RequestBody @Valid GameAreaServicePageDTO dto) {
        return ApiResultVO.ok(baseService.myPage(dto));
    }

    @Operation(summary = "通过主键id，查看详情")
    @PostMapping("/infoById")
    @PreAuthorize("hasAuthority('gameAreaService:infoById')")
    public ApiResultVO<GameAreaServiceDO> infoById(@RequestBody @Valid NotNullId notNullId) {
        return ApiResultVO.ok(baseService.infoById(notNullId));
    }

    @Operation(summary = "批量删除")
    @PostMapping("/deleteByIdSet")
    @PreAuthorize("hasAuthority('gameAreaService:deleteByIdSet')")
    public ApiResultVO<String> deleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.deleteByIdSet(notEmptyIdSet));
    }

    @Operation(summary = "用户，分页排序查询")
    @PostMapping("/user/page")
    public ApiResultVO<Page<GameAreaServiceDO>> userPage(@RequestBody @Valid GameAreaServiceUserPageDTO dto) {
        return ApiResultVO.ok(baseService.userPage(dto));
    }

    @Operation(summary = "用户，新增，游戏用户")
    @PostMapping("/user/gameUser/insert")
    public ApiResultVO<String> userGameUserInsert(@RequestBody @Valid GameAreaServiceGameUserInsertDTO dto) {
        return ApiResultVO.ok(baseService.userGameUserInsert(dto));
    }

    @Operation(summary = "用户，批量删除，游戏用户")
    @PostMapping("/user/gameUser/deleteByIdSet")
    public ApiResultVO<String> userGameUserDeleteByIdSet(@RequestBody @Valid NotEmptyIdSet notEmptyIdSet) {
        return ApiResultVO.ok(baseService.userGameUserDeleteByIdSet(notEmptyIdSet));
    }

    @Operation(summary = "用户，分页排序查询，当前区服下的游戏用户")
    @PostMapping("/user/gameUser/page")
    public ApiResultVO<Page<GameUserDO>> userGameUserPage(@RequestBody @Valid GameAreaServiceUserGameUserPageDTO dto) {
        return ApiResultVO.ok(baseService.userGameUserPage(dto));
    }

    @Operation(summary = "用户，获取当前区服下的游戏用户 jwt")
    @PostMapping("/user/gameUser/jwt")
    public ApiResultVO<String> userGameUserJwt(@RequestBody @Valid GameAreaServiceUserGameUserJwtDTO dto) {
        return ApiResultVO.ok(BaseBizCodeEnum.OK, baseService.userGameUserJwt(dto));
    }

}
