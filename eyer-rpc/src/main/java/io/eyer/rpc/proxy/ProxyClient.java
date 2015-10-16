package io.eyer.rpc.proxy;

import io.eyer.rpc.invoker.Invoker;
import io.eyer.rpc.proxy.meta.Request;
import io.eyer.rpc.proxy.meta.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Created by 青木 on 2015/8/24.
 */
public class ProxyClient implements Closeable {
    private static Logger logger = LoggerFactory.getLogger(ProxyClient.class);
    private final Invoker invoker;


    public ProxyClient(Invoker invoker) {
        this.invoker = invoker;
    }


    public <T> T refService(Class<T> klass) throws IOException, InterruptedException {
        return (T) Proxy.newProxyInstance(klass.getClassLoader(), new Class[]{klass}, (proxy,method,args)->{
            Request request = ProxyClient.makeRequest(klass, method, args);
            Response response = invoker.invoke(request);
            if (response.getException() != null) throw response.getException();
            return response.getResult();
        });
    }



    @Override
    public void close() throws IOException {
        invoker.close();
    }

    public static <T> Request makeRequest(Class<T> klass, Method method, Object[] args) {
        Request request = new Request();
        request.setId(UUID.randomUUID().toString());
        request.setInterfaceClass(klass);
        request.setParams(args);
        Class<?>[] parameterTypes = method.getParameterTypes();
        request.setPatamType(parameterTypes);
        request.setMethodName(method.getName());
        return request;
    }


}
