package zyz.free.service.genid;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import zyz.free.data.mapper.IdSegmentEntityMapper;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
@AllArgsConstructor
public class GenIdService {


    private final IdSegmentEntityMapper idSegmentEntityMapper;
    private final ConcurrentHashMap<String, MysqlGenIdLeafServiceImpl> bizLeafServiceMap = new ConcurrentHashMap<>();


    /**
     * 根据业务标签获取相应的id，这样可以动态的创建相应的服务，而不需要停服，
     *
     * @param bizTag
     * @return
     */
    public Long genId(String bizTag) {
        Long id = getIdLeafService(bizTag).genId();
        log.info("生成分布式id:bizTag={},Id={}", bizTag, id);
        return id;
    }


    private MysqlGenIdLeafServiceImpl getIdLeafService(String bizTag) {
        if (bizLeafServiceMap.get(bizTag) == null) {
            synchronized (bizLeafServiceMap) {
                if (bizLeafServiceMap.get(bizTag) == null) {
                    MysqlGenIdLeafServiceImpl idLeafService = new MysqlGenIdLeafServiceImpl(bizTag, idSegmentEntityMapper, true);
                    bizLeafServiceMap.putIfAbsent(bizTag, idLeafService);
                }
            }
        }
        return bizLeafServiceMap.get(bizTag);
    }


}
