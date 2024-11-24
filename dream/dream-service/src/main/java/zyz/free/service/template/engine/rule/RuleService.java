package zyz.free.service.template.engine.rule;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import zyz.free.data.entity.ExpressionRuleEntity;
import zyz.free.data.repository.ExpressionRuleRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class RuleService {

    private final ExpressionRuleRepository expressionRuleRepository;

    public RuleResult matchRule() {
        // 准备校验的所有参数
        RuleEngineDataHolder dataHolder = new RuleEngineDataHolder();
        dataHolder.setAge(11);
        dataHolder.setName("");
        dataHolder.setCount(0L);

        List<ExpressionRuleEntity> expressionRuleEntities = expressionRuleRepository.ListByGroup("TEST");
        RuleResult match = RuleEngine.match(dataHolder, expressionRuleEntities);

        return match;
    }

}
