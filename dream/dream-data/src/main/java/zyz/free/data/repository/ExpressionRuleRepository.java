package zyz.free.data.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import zyz.free.data.entity.ExpressionRuleEntity;
import zyz.free.data.mapper.ExpressionRuleEntityMapper;

import java.util.List;

@Repository
@AllArgsConstructor
public class ExpressionRuleRepository {

    private final ExpressionRuleEntityMapper expressionRuleEntityMapper;


    public List<ExpressionRuleEntity> ListByGroup(String ruleGroup) {
        return expressionRuleEntityMapper.listByRuleGroup(ruleGroup);
    }

    public void insert(ExpressionRuleEntity entity) {
        expressionRuleEntityMapper.insert(entity);
    }

}
