package zyz.free.model.enums;

import lombok.Getter;

public enum OrderStateEnum {

    INIT("INIT", "初始化"),
    PROCESSING("PROCESSING", "执行中"),
    SUCCEED("SUCCEED", "成功"),
    FAILED("FAILED", "失败"),
    ;


    @Getter
    private String code;
    private String desc;

    private OrderStateEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
