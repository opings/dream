package zyz.free.data.mapper;

import org.apache.ibatis.annotations.Param;
import zyz.free.data.entity.IdSegmentEntity;

public interface IdSegmentEntityMapper {

    IdSegmentEntity selectByBizTag(@Param("bizTag") String bizTag);

    int updateByBizTagAndMaxId(IdSegmentEntity record);
//
//    int updateByPrimaryKey(IdSegmentEntity record);
}