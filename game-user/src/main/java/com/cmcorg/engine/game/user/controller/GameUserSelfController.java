package com.cmcorg.engine.game.user.controller;

import com.cmcorg.engine.game.user.model.dto.GameUserSelfUpdateInfoDTO;
import com.cmcorg.engine.game.user.model.vo.GameUserSelfInfoVO;
import com.cmcorg.engine.game.user.service.GameUserSelfService;
import com.cmcorg.engine.web.auth.model.vo.ApiResultVO;
import com.cmcorg.engine.web.model.generate.model.annotation.WebPage;
import com.cmcorg.engine.web.model.generate.model.enums.PageTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@WebPage(type = PageTypeEnum.NONE)
@RestController
@RequestMapping(value = "/game/user/self")
@Tag(name = "游戏用户-自我-管理")
public class GameUserSelfController {

    @Resource
    GameUserSelfService baseService;

    @Operation(summary = "获取：当前游戏用户，基本信息")
    @PostMapping(value = "/info")
    public ApiResultVO<GameUserSelfInfoVO> gameUserSelfInfo() {
        return ApiResultVO.ok(baseService.gameUserSelfInfo());
    }

    @Operation(summary = "当前游戏用户：基本信息：修改")
    @PostMapping(value = "/updateInfo")
    public ApiResultVO<String> gameUserSelfUpdateInfo(@RequestBody @Valid GameUserSelfUpdateInfoDTO dto) {
        return ApiResultVO.ok(baseService.gameUserSelfUpdateInfo(dto));
    }

}
