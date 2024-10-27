package zyz.free.controller.genid;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;
import zyz.free.genid.api.GenIdApi;
import zyz.free.genid.response.SimpleResponse;
import zyz.free.sdk.idleaf.IdService;
import zyz.free.service.genid.GenIdService;

@RestController
@AllArgsConstructor
@Log4j2
public class GenIdController implements GenIdApi {

    private final GenIdService genIdService;
    private final IdService idService;






    @Override
    public SimpleResponse<Long> GenId() {
        Long genId = genIdService.genId("test");
//        Long genId =idService.genId("test");
        return SimpleResponse.success(genId);
    }


}
