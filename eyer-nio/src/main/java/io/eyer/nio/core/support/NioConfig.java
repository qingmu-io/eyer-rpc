package io.eyer.nio.core.support;

import io.eyer.nio.core.serializer.Serializer;
import io.eyer.nio.core.net.Connector;
import io.eyer.nio.core.session.SessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2015/8/28.
 */
public class NioConfig {
    private static Logger logger = LoggerFactory.getLogger(NioConfig.class);
    private static Serializer serializer;
    private static Handler handler;
    private static SessionHandler sessionHandler;
    private static Connector connector;

    public static void registered(Serializer serializer) {
        logger.info("registered serializer [{}]",serializer);
        NioConfig.serializer = serializer;
    }

    public static void registered(Handler handler) {
        logger.info("registered handler [{}]",handler);
        NioConfig.handler = handler;
    }


    public static void registered(Connector connector){
        logger.info("registered connector [{}]",connector);
        if(connector.getState() == Thread.State.NEW) connector.start();
        NioConfig.connector = connector;}

    public static Serializer getSerializer() {
        if (serializer == null)
            throw new NullPointerException("Serializer is null. please invoke NioConfig.registered(Serializer serializer)");
        return serializer;
    }

    public static Handler getHandler() {
        if (handler == null)
            throw new NullPointerException("handler is null. please invoke NioConfig.registered(Dandler handler)");
        return handler;
    }

    public static Connector getConnector(){
        if (connector == null)
            throw new NullPointerException("Connector is null. please invoke NioConfig.registered(Connector connector)");
            return connector;
    }

    public static SessionHandler getSessionHandler() {
        return sessionHandler;
    }

    public static void initSessionHandler() {
        NioConfig.sessionHandler = new SessionHandler();
        logger.info("init SessionHandler [{}]",sessionHandler);
    }
}
