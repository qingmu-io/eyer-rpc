package io.eyer.rpc.handler;

import io.eyer.nio.core.session.Session;
import io.eyer.nio.core.support.Handler;
import io.eyer.rpc.proxy.meta.Response;
import io.eyer.rpc.ticket.Ticket;
import io.eyer.rpc.ticket.TicketMananger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by 青木 on 2015/8/18.
 */
public class ClientHandler extends Handler {

    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void onPacket(Session session, Object obj) {
        if(obj instanceof  Response){
            Response response = (Response) obj;
//            logger.info("on packet {}", response);
            Ticket ticket = TicketMananger.removeTicket(response.getId());
            if (ticket != null) {
                ticket.notifyResponse(response);
            }
        }

    }
}
