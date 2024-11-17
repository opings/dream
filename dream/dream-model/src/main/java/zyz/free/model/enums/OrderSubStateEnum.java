package zyz.free.model.enums;

import lombok.Getter;

public enum OrderSubStateEnum {

    BEGIN("BEGIN", "子状态开始"),
//    END("END", "子状态结束"),
    WAITING_MANUAL_CHECK_1("WAITING_MANUAL_CHECK_1", "CHECK_1等待人工操作"),
    CHECK_1_PASS("CHECK_1_PASS", "CHECK_1 PASS"),
    WAITING_MANUAL_CHECK_2("WAITING_MANUAL_CHECK_2", "CHECK_2等待人工操作"),
    ;


    @Getter
    private String code;
    private String desc;

    private OrderSubStateEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
