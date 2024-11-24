package zyz.free.service.template.engine.rule;

import lombok.Data;

@Data
public class RuleResult {

    private Boolean match;

    private String rejectRuleCode;

    private String errorCode;

    private String errorMsg;

}
