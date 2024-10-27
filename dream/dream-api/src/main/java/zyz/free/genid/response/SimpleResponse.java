package zyz.free.genid.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class SimpleResponse<T> implements Serializable {


    private static String successCode = "200";

    private String code;

    private String msg;

    private T data;


    public static <T> SimpleResponse<T> success(T result) {
        SimpleResponse<T> simpleResponse = new SimpleResponse<>();
        simpleResponse.setCode(successCode);
        simpleResponse.setData(result);
        return simpleResponse;
    }

    public static <T> SimpleResponse<T> failure(String errorCode, String errorMsg) {
        SimpleResponse<T> simpleResponse = new SimpleResponse<>();
        simpleResponse.setCode(errorCode);
        simpleResponse.setMsg(errorMsg);
        return simpleResponse;
    }
}
