package com.cmcorg.engine.game.areaservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg.engine.game.areaservice.model.dto.*;
import com.cmcorg.engine.game.areaservice.model.entity.GameAreaServiceDO;
import com.cmcorg.engine.game.user.model.entity.GameUserDO;
import com.cmcorg.engine.web.model.model.dto.NotEmptyIdSet;
import com.cmcorg.engine.web.model.model.dto.NotNullId;

public interface GameAreaServiceService extends IService<GameAreaServiceDO> {

    String insertOrUpdate(GameAreaServiceInsertOrUpdateDTO dto);

    Page<GameAreaServiceDO> myPage(GameAreaServicePageDTO dto);

    GameAreaServiceDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    Page<GameAreaServiceDO> userPage(GameAreaServiceUserPageDTO dto);

    String userGameUserInsert(GameAreaServiceGameUserInsertDTO dto);

    String userGameUserDeleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    Page<GameUserDO> userGameUserPage(GameAreaServiceUserGameUserPageDTO dto);

    String userGameUserJwt(GameAreaServiceUserGameUserJwtDTO dto);

}
