package zyz.free.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import zyz.free.data.entity.IdSegmentEntity;
import zyz.free.data.mapper.IdSegmentEntityMapper;
import zyz.free.genid.response.SimpleResponse;
import zyz.free.service.client.redis.RedisCacheClient;
import zyz.free.service.template.engine.rule.RuleResult;
import zyz.free.service.template.engine.rule.RuleService;
import zyz.free.util.JacksonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@Log4j2
public class TestController {


    private final IdSegmentEntityMapper idSegmentEntityMapper;

    private final RedisCacheClient redisCacheClient;


    private final RuleService ruleService;

    @ApiOperation("Easy Rule test")
    @GetMapping("/v1/easy-rule/test")
    public SimpleResponse<RuleResult> easyRuleTest() {
        RuleResult ruleResult = ruleService.matchRule();
        return SimpleResponse.success(ruleResult);
    }


    @ApiOperation("QPS test")
    @GetMapping("/v1/qps/test")
    public SimpleResponse<String> qpsTest() {
//        Meter meter = registry.meter("test");
//        for (int i = 0; i < 120; i++) {
//            meter.mark();
//        }
//        System.out.println(meter.getMeanRate());
//        System.out.println(meter.getOneMinuteRate());

        return SimpleResponse.success("Y");
    }

    @ApiOperation("获取gen id")
    @GetMapping("/v1/test")
    public SimpleResponse<String> test() {
        IdSegmentEntity test = idSegmentEntityMapper.selectByBizTag("test");
        return SimpleResponse.success(JacksonUtils.toJson(test));
    }

    @ApiOperation("redis tes")
    @PostMapping("/v1/redis/test")
    public SimpleResponse<String> redisTest(@RequestBody Map<String, String> request) {

        String nameSpace = request.get("nameSpace");
        String key = request.get("key");

        List<IdSegmentEntity> result = redisCacheClient.get(
                nameSpace,
                key,
                () -> {
                    ArrayList<IdSegmentEntity> list = new ArrayList<>();
                    for (int i = 0; i < 3; i++) {
                        IdSegmentEntity idSegmentEntity = new IdSegmentEntity();
                        idSegmentEntity.setId((long) i);
                        idSegmentEntity.setBizTag("teg");
                        list.add(idSegmentEntity);
                    }
                    return list;
                },
                60);
        return SimpleResponse.success(JacksonUtils.toJson(result));
    }

}
