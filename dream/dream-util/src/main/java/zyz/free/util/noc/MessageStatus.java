package zyz.free.util.noc;

import com.binance.master.enums.BaseEnum;

import static us.binance.infra.notification.apppush.api.ByteUtils.byteOf;


public enum MessageStatus implements BaseEnum {
    SAVED(byteOf(0), "saved", "saved"),
    SENT(byteOf(1), "sent", "sent"),
    RECEIVED(byteOf(5), "received", "received by system"),
    CLICKED(byteOf(6), "clicked", "clicked by user"),
    DISMISSED(byteOf(8), "dismissed", "dismissed by users"),
    DUP_SEND(byteOf(-1), "duplicate_send", "duplicate send for same device"),
    SWITCH_CLOSED(byteOf(-2), "switch_closed", "notification switch is closed"),
    SEND_FAILED(byteOf(-9), "send_failed", "send failed in push center"),
    RESPONSE_ERROR(byteOf(-10), "response_error", "get response error from app-push"),
    DISTRIBUTE_FAILED(byteOf(-19), "distribute_failed", "distribute failed");

    private final byte byteValue;
    private final String code;
    private final String description;

    MessageStatus(byte byteValue, String code, String description) {
        this.byteValue = byteValue;
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return description;
    }

    public byte getByteValue() {
        return byteValue;
    }
}
