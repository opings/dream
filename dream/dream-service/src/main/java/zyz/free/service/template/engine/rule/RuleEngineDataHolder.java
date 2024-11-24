package zyz.free.service.template.engine.rule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleEngineDataHolder {

    private Integer age;

    private String name;

    private Long count;



}
