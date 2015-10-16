package io.eyer.rpc.ticket;

import io.eyer.rpc.proxy.meta.Request;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2015/8/18.
 */
public class TicketMananger {
    private static final ConcurrentHashMap<String, Ticket> tickets = new ConcurrentHashMap<>();

    public static final Ticket getTicket(String id) {
        if (id == null) return null;
        return tickets.get(id);
    }


    public static final Ticket createTicket(Request req, long timeout) {
        Ticket ticket = new Ticket(req, timeout);
        if (tickets.putIfAbsent(ticket.getId(), ticket) != null) {
            throw new IllegalArgumentException("duplicate ticket number.");
        }
        return ticket;
    }

    public static final Ticket removeTicket(String id) {
        return tickets.remove(id);
    }

}
