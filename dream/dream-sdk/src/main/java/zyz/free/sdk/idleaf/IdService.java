package zyz.free.sdk.idleaf;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @author zyz
 * @since 2020-03-18 09:29
 */
@Service
@Log4j2
@AllArgsConstructor
public class IdService {


    private final IdLeafServiceFactory idLeafServiceFactory;


    /**
     * 根据业务标签获取相应的id，这样可以动态的创建相应的服务，而不需要停服，
     *
     * @param bizTag
     * @return
     */
    public Long genId(String bizTag) {
        Long id = idLeafServiceFactory.genId(bizTag);
        log.info("生成分布式id:bizTag={},Id={}", bizTag, id);
        return id;
    }


    /**
     * 根据业务标签获取相应的id，这样可以动态的创建相应的服务，而不需要停服，
     *
     * @param bizTag
     * @return
     */
    public Long genId(String bizTag, Long userId, Integer dbSize) {
        return idLeafServiceFactory.genId(bizTag, userId, dbSize);
    }




}
