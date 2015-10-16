package io.eyer.rpc.proxy.meta;

import java.io.Serializable;

/**
 * Created by 青木 on 2015/8/18.
 */
public class Response implements Serializable {

    private String id;
    private Object result;
    private Throwable exception;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "Response{" +
                "id='" + id + '\'' +
                ", result=" + result +
                ", exception=" + exception +
                '}';
    }
}
