package zyz.free.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;

public class DreamBeanUtils {


    /**
     * 把 {@param source}的非空的字段拷贝到{@param target}
     */
    public static void copyProperties(Object source, Object target) {
        BeanUtil.copyProperties(source, target, CopyOptions.create().setIgnoreNullValue(true));
    }


}
