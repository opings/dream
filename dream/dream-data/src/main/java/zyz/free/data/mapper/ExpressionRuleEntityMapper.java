package zyz.free.data.mapper;

import org.apache.ibatis.annotations.Param;
import zyz.free.data.entity.ExpressionRuleEntity;

import java.util.List;

public interface ExpressionRuleEntityMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ExpressionRuleEntity record);


    ExpressionRuleEntity selectByPrimaryKey(Long id);

    List<ExpressionRuleEntity> listByRuleGroup(@Param("ruleGroup") String ruleGroup);


    int updateByPrimaryKeySelective(ExpressionRuleEntity record);

}