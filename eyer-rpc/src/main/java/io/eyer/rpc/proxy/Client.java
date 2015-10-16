package io.eyer.rpc.proxy;

import io.eyer.nio.core.net.Connector;
import io.eyer.nio.core.session.Session;
import io.eyer.nio.core.support.NioConfig;
import io.eyer.rpc.proxy.meta.Heartbeat;
import io.eyer.rpc.proxy.meta.Request;
import io.eyer.rpc.proxy.meta.Response;
import io.eyer.rpc.ticket.Ticket;
import io.eyer.rpc.ticket.TicketMananger;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by 青木 on 2015/8/18.
 */
public class Client implements Closeable {

    private Session session;
    private long timeout = 3000;
    private InetSocketAddress remoteAddress;

    public Client(InetSocketAddress remoteAddress) throws Exception {
       this.remoteAddress = remoteAddress;
        final Connector connector = NioConfig.getConnector();
        session = connector.connect(this.remoteAddress);
    }


    public Response invoker(Request request) throws InterruptedException, IOException {
        Ticket ticket = TicketMananger.createTicket(request, timeout);
        try {
            this.session.write(request);
            if (!ticket.await(timeout, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("time out");
            }
            return ticket.response();
        } catch (Exception e) {
//            this.session.close();
          throw new RuntimeException(e.getMessage(),e);
        }

    }

    static Heartbeat heartbeat = new Heartbeat();

    public void heartbeat() {
        this.session.write(heartbeat);
    }


    public void close() {
        try {
            this.session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Client{" +
                ", session=" + session +
                ", timeout=" + timeout +
                '}';
    }
}
