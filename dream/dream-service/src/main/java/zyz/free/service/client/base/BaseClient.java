package zyz.free.service.client.base;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.Callable;

@Slf4j
public class BaseClient {

    protected <T> T execute(String apiInfo,
                            Object request,
                            Callable<T> callable,
                            boolean ignoreNull,
                            boolean ignoreException) {
        log.info("BaseClient execute start. apiInfo:{}, request:{}", apiInfo, request);

        try {
            T result = callable.call();
            if (Objects.isNull(result) && !ignoreNull) {
                throw new RuntimeException("response is null");
            }
            log.info("BaseClient execute end. apiInfo:{}, request:{} response:{}", apiInfo, request, result);
            return result;
        } catch (Exception e) {
            log.info("BaseClient execute exception. apiInfo:{}, request:{}", apiInfo, request, e);
            if (ignoreException) {
                return null;
            }
            throw new RuntimeException("api call exception");
        }

    }
}
