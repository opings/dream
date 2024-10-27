package zyz.free.genid.api;


import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import zyz.free.genid.response.SimpleResponse;

//@FeignClient(name = "")
public interface GenIdApi {

    @ApiOperation("获取gen id")
    @PostMapping("/v1/gen-id/create")
    SimpleResponse<Long> GenId();


}