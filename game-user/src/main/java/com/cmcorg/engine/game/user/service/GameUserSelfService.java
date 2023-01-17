package com.cmcorg.engine.game.user.service;

import com.cmcorg.engine.game.user.model.dto.GameUserSelfUpdateInfoDTO;
import com.cmcorg.engine.game.user.model.vo.GameUserSelfInfoVO;

public interface GameUserSelfService {

    GameUserSelfInfoVO gameUserSelfInfo();

    String gameUserSelfUpdateInfo(GameUserSelfUpdateInfoDTO dto);

}
