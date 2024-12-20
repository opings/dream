package zyz.free.model.enums;

import lombok.Getter;

public enum OrderOperationEnum {

    INIT("INIT", "初始化"),
//    PROCESSING("PROCESSING", "执行中"),
//    SUCCEED("SUCCEED", "成功"),
//    FAILED("FAILED", "失败"),
    ;


    @Getter
    private String code;
    private String desc;

    private OrderOperationEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
