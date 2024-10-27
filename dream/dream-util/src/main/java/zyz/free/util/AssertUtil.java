package zyz.free.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 断言工具类
 */
public class AssertUtil {

    /**
     * 校验字符串非空
     *
     * @param str
     */
    public static void notBlank(String str) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException("str empty");
        }
    }

    /**
     * 校验字符串非空
     *
     * @param str
     * @param msgSupplier
     */
    public static void notBlank(String str, Supplier<String> msgSupplier) {
        if (StringUtils.isBlank(str)) {
            throw new IllegalArgumentException(msgSupplier.get());
        }
    }


    /**
     * 校验对象非null
     *
     * @param obj
     */
    public static void notNull(Object obj) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException("obj null");
        }
    }


    /**
     * 校验对象非null
     *
     * @param obj
     * @param msgSupplier
     */
    public static void notNull(Object obj, Supplier<String> msgSupplier) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException(msgSupplier.get());
        }
    }


    /**
     * 校验表达式真
     *
     * @param expression
     * @param msgSupplier
     */
    public static void isTrue(boolean expression, Supplier<String> msgSupplier) {
        if (!expression) {
            throw new IllegalStateException(msgSupplier.get());
        }
    }


    /**
     * 校验相等
     *
     * @param obj1
     * @param obj2
     * @param msgSupplier
     */
    public static void equals(Object obj1, Object obj2, Supplier<String> msgSupplier) {
        isTrue(equals(obj1, obj2), msgSupplier);
    }

    /**
     * 两个对象是否相等
     *
     * @param obj1
     * @param obj2
     * @return
     */
    private static boolean equals(Object obj1, Object obj2) {
        if (obj1 == null) {
            return null == obj2;
        }
        return obj1.equals(obj2);
    }


}