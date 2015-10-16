package io.eyer.nio.core.support;

import io.eyer.nio.core.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2015/8/28.
 */
public abstract class Handler {
    private static Logger logger = LoggerFactory.getLogger(Handler.class);
    public abstract void onPacket(Session session, Object obj);
    public void onException(Session session, Throwable e) {
        session.status = Session.Status.ERROR;
    }
    public void onSessionCreated(Session session) {
        session.status = Session.Status.OPEN;
        logger.info("session on created session [{}]",session);
    }

    public void onCodecException(String message, Exception e) {

    }


}
