package com.cmcorg.engine.game.user.service.impl;

import com.cmcorg.engine.game.auth.util.GameAuthUserUtil;
import com.cmcorg.engine.game.user.model.dto.GameUserSelfUpdateInfoDTO;
import com.cmcorg.engine.game.user.model.entity.GameUserDO;
import com.cmcorg.engine.game.user.model.vo.GameUserSelfInfoVO;
import com.cmcorg.engine.game.user.service.GameUserSelfService;
import com.cmcorg.engine.game.user.service.GameUserService;
import com.cmcorg.engine.web.auth.exception.BaseBizCodeEnum;
import com.cmcorg.engine.web.auth.model.entity.BaseEntity;
import com.cmcorg.engine.web.auth.util.MyEntityUtil;
import com.cmcorg.engine.web.util.util.NicknameUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GameUserSelfServiceImpl implements GameUserSelfService {

    @Resource
    GameUserService gameUserService;

    /**
     * 获取：当前游戏用户，基本信息
     */
    @Override
    public GameUserSelfInfoVO gameUserSelfInfo() {

        GameUserDO gameUserDO =
            gameUserService.lambdaQuery().eq(BaseEntity::getId, GameAuthUserUtil.getCurrentGameUserId())
                .select(GameUserDO::getAreaServiceId, GameUserDO::getNickname, GameUserDO::getAvatarUri,
                    GameUserDO::getBio).one();

        GameUserSelfInfoVO gameUserSelfInfoVO = new GameUserSelfInfoVO();
        gameUserSelfInfoVO.setNickname(gameUserDO.getNickname());
        gameUserSelfInfoVO.setBio(gameUserDO.getBio());
        gameUserSelfInfoVO.setAvatarUri(gameUserDO.getAvatarUri());
        gameUserSelfInfoVO.setAreaServiceId(gameUserDO.getAreaServiceId());

        return gameUserSelfInfoVO;

    }

    /**
     * 当前游戏用户：基本信息：修改
     */
    @Override
    public String gameUserSelfUpdateInfo(GameUserSelfUpdateInfoDTO dto) {

        Long currentGameUserId = GameAuthUserUtil.getCurrentGameUserId();

        GameUserDO gameUserDO = new GameUserDO();
        gameUserDO.setId(currentGameUserId);
        gameUserDO.setNickname(MyEntityUtil.getNotNullStr(dto.getNickname(), NicknameUtil.getRandomNickname()));
        gameUserDO.setBio(MyEntityUtil.getNotNullAndTrimStr(dto.getBio()));
        gameUserDO.setAvatarUri(MyEntityUtil.getNotNullStr(dto.getAvatarUri()));

        gameUserService.updateById(gameUserDO);

        return BaseBizCodeEnum.OK;

    }

}
