package io.eyer.nio.core.net;

import io.eyer.nio.core.session.Session;
import io.eyer.nio.core.support.NioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by 青木 on 2015/8/17.
 */
public class Reactor extends Thread implements Closeable {
    private static Logger logger = LoggerFactory.getLogger(Reactor.class);
    private Selector selector;
    private Queue<Object[]> regQueue = new ConcurrentLinkedQueue<>();


    public Reactor(String name) throws IOException {
        super(name);
        this.selector = Selector.open();
    }

    public void register(SocketChannel socketChannel, int ops) throws ClosedChannelException {
        if (this == currentThread()) {
            SelectionKey key = socketChannel.register(this.selector, ops);
            Session session = new Session(socketChannel, key, new Date());
            key.attach(session);
            NioConfig.getHandler().onSessionCreated(session);
        } else {
            regQueue.offer(new Object[]{socketChannel, ops, null});
            this.selector.wakeup();
        }
    }

    @Override
    public void run() {
        while (!interrupted()) {
            try {
                this.selector.select();
                this.handleReqQ(selector);
                Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
                selectionKeys.parallelStream().forEach(this::handleRWEventKey);
                selectionKeys.clear();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        try {
            close0(this.selector);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    private void close0(Selector selector) throws IOException {
        if(selector.isOpen()){
            this.selector.select(1L);
            this.selector.selectedKeys().forEach(k->{try{k.cancel();k.channel().close();}catch (Exception e){logger.error(e.getMessage(),e);}});
        }
    }

    private void handleReqQ(Selector selector) {
        Object[] item = null;
        while ((item = this.regQueue.poll()) != null) {
            try {
                SocketChannel socketChannel = (SocketChannel) item[0];
                Session session = (Session) item[2];
                if (session != null) {
                    SelectionKey key = socketChannel.register(selector, (int) item[1], session);
                    session.setKey(key);
                    session.getLatch().countDown();
                } else {
                    SelectionKey key = socketChannel.register(selector, (int) item[1]);
                    session = new Session(socketChannel, key, new Date());
                    key.attach(session);
                }
                NioConfig.getHandler().onSessionCreated(session);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void handleRWEventKey(SelectionKey key) {
        try {
            if (key.isValid()) {
                Session session = (Session) key.attachment();
                session.updateLastTime();
                if (key.isReadable()) {
                    NioConfig.getSessionHandler().handleRead(session);
                } else if (key.isWritable()) {
                    NioConfig.getSessionHandler().handleWrite(session);
                } else {
                    key.cancel();
                }
            }
        } catch (Exception e) {
            try {
                key.channel().close();
                key.cancel();
            } catch (IOException e1) {
                logger.error(e.getMessage(), e);
            }
            logger.error(e.getMessage(), e);

        }
    }

    public void registerSess(SocketChannel socketChannel, int ops, Session session) throws ClosedChannelException {
        if (this == currentThread()) {
            SelectionKey key = socketChannel.register(this.selector, ops, session);
            session.setKey(key);
        } else {
            Reactor.this.regQueue.offer(new Object[]{socketChannel, ops, session});
            Reactor.this.selector.wakeup();
        }
    }

    @Override
    public void close() {
        this.interrupt();
    }
}
