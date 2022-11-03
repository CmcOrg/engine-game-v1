package com.cmcorg.engine.game.room.current.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg.engine.game.room.current.model.dto.GameRoomCurrentPageDTO;
import com.cmcorg.engine.game.room.current.model.entity.GameRoomCurrentDO;
import com.cmcorg.engine.game.room.current.model.vo.GameRoomCurrentPageVO;
import org.apache.ibatis.annotations.Param;

public interface GameRoomCurrentMapper extends BaseMapper<GameRoomCurrentDO> {

    // 分页排序查询
    Page<GameRoomCurrentPageVO> myPage(@Param("page") Page<GameRoomCurrentPageVO> page,
        @Param("dto") GameRoomCurrentPageDTO dto);

}
