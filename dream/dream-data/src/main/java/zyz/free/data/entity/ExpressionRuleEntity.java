package zyz.free.data.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ExpressionRuleEntity {
    private Long id;

    private String ruleGroup;

    private String ruleCode;

    private String ruleName;

    private String ruleDesc;

    private Integer priority;

    private String whenExpression;

    private String thenExpression;

    private Date dbCreateTime;

    private Date dbModifyTime;

    private Byte deleted;

}