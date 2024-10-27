package zyz.free.genid.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class SimpleRequest<T> implements Serializable {


    private T data;


}
