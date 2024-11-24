package zyz.free.service.template.engine.rule;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngineParameters;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRule;
import org.springframework.stereotype.Component;
import zyz.free.data.entity.ExpressionRuleEntity;

import java.util.List;

@Component
public class RuleEngine {


    public static RuleResult match(RuleEngineDataHolder ruleEngineDataHolder,
                                   List<ExpressionRuleEntity> entityListList) {
        RuleResult result = new RuleResult();


        Facts facts = new Facts();
        facts.put("fact", ruleEngineDataHolder);
        facts.put("result", result);

        Rules rules = new Rules();
        for (ExpressionRuleEntity entity : entityListList) {
            rules.register(buildMVELRule(entity));
        }

        RulesEngineParameters rulesEngineParameters = new RulesEngineParameters()
                .skipOnFirstAppliedRule(true)
                .skipOnFirstFailedRule(false)
                .skipOnFirstNonTriggeredRule(false);

        DefaultRulesEngine defaultRulesEngine = new DefaultRulesEngine(rulesEngineParameters);
        defaultRulesEngine.fire(rules, facts);

        return result;
    }


    private static MVELRule buildMVELRule(ExpressionRuleEntity entity) {
        MVELRule rule = new MVELRule()
                .name(entity.getRuleCode())
                .priority(entity.getPriority())
                .description(entity.getRuleDesc())
                .when(entity.getWhenExpression())
                .then(entity.getThenExpression());
        return rule;
    }


}
