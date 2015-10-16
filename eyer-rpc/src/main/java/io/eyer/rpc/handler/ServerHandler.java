package io.eyer.rpc.handler;

import io.eyer.nio.core.session.Session;
import io.eyer.nio.core.support.Handler;
import io.eyer.rpc.proxy.meta.Request;
import io.eyer.rpc.proxy.meta.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 青木 on 2015/8/18.
 */
public class ServerHandler extends Handler {
    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    ExecutorService executorService = Executors.newFixedThreadPool(50);
    public static ConcurrentHashMap<Class<?>, Object> services = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Method> methodConcurrentHashMap = new ConcurrentHashMap<>();
    @Override
    public void onPacket(Session session, Object obj) {

        executorService.execute(() -> {
            Request request = (Request) obj;
            Response response = new Response();
            response.setId(request.getId());
            try {
//                logger.info(request.toString());
                Class<?> interfaceClass = request.getInterfaceClass();
                response.setResult(findMethod(request).invoke(services.get(interfaceClass), request.getParams()));
            }catch (NullPointerException e){
                logger.error(e.getMessage(), e);
                response.setException(e);
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                response.setException(e);
                try {session.close(); } catch (IOException e1) { logger.error(e1.getMessage(), e1);}
            }finally {
                session.write(response);
            }
        });

    }

    private Method findMethod(Request request) throws NoSuchMethodException {
        String key = request.getInterfaceClass().getName() + request.getMethodName();
        if (!this.methodConcurrentHashMap.contains(key))
            this.methodConcurrentHashMap.put(key, request.getInterfaceClass().getMethod(request.getMethodName(), request.getPatamType()));
        return this.methodConcurrentHashMap.get(key);
    }
}
