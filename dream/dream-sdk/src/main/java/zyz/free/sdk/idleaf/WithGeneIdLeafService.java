package zyz.free.sdk.idleaf;


import zyz.free.util.AssertUtil;

/**
 * @author zyz
 * @since 2020-03-18 09:29
 */
public class WithGeneIdLeafService {


    private IdLeafService idLeafService;

    /**
     * 分库个数
     */
    private Integer dbSize;


    public WithGeneIdLeafService(IdLeafService idLeafService, Integer dbSize) {
        AssertUtil.notNull(idLeafService, () -> "idLeafService null");
        AssertUtil.notNull(dbSize, () -> "dbSize null");
        AssertUtil.isTrue(dbSize % 2 == 0, () -> "dbSize 必须是2的倍数");
        this.idLeafService = idLeafService;
        this.dbSize = dbSize;
    }


    /**
     * 1010
     *
     * @param userId
     * @return
     */
    public Long getId(Long userId) {
        Long prefixOrderId = idLeafService.genId();
        int leftMoveBit = leftMoveBit();

        /**
         * 加入userId基因
         */
        Long lastOrderId = ((prefixOrderId << leftMoveBit) | (userId % dbSize));
        return lastOrderId;
    }


    private int leftMoveBit() {
        return Integer.toBinaryString(dbSize).length() - 1;
    }
}
