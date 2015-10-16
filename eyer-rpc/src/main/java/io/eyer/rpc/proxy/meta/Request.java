package io.eyer.rpc.proxy.meta;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by 青木 on 2015/8/18.
 */
public class Request implements Serializable {

    private String id;
    /**
     * 参数
     */
    private Object[] params;

    /**
     * 参数类型
     */
    private Class<?>[] patamType;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 需要进行调用的接口
     */
    private Class<?> interfaceClass;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Class<?>[] getPatamType() {
        return patamType;
    }

    public void setPatamType(Class<?>[] patamType) {
        this.patamType = patamType;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id='" + id + '\'' +
                ", params=" + Arrays.toString(params) +
                ", patamType=" + Arrays.toString(patamType) +
                ", methodName='" + methodName + '\'' +
                ", interfaceClass=" + interfaceClass +
                '}';
    }
}
