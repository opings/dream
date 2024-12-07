package zyz.free.util.noc;

import cn.jiguang.common.resp.BaseResult;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jiguang.common.utils.StringUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NewBaseResult extends BaseResult {

    public static <T extends BaseResult> T fromResponse(ResponseWrapper responseWrapper, Class<T> clazz) {
        T result;

        log.info("raw response from jiguang is: {} .", responseWrapper.responseContent);
        if (responseWrapper.isServerResponse() && StringUtils.isNotEmpty(responseWrapper.responseContent)) {
            result = _gson.fromJson(responseWrapper.responseContent, clazz);
        } else {
            try {
                result = clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        result.setResponseWrapper(responseWrapper);

        return result;
    }
}
