package io.eyer.nio.core.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.*;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by 青木 on 2015/8/17.
 */
public class Acceptor extends Thread implements Closeable{
    private static Logger logger = LoggerFactory.getLogger(Acceptor.class);
    private final Selector selector;
    private final ReactorPool reactorPool;
    private Queue<ServerSocketChannel> regQ = new ConcurrentLinkedQueue<>();

    public Acceptor(String name, ReactorPool reactorPool) throws IOException {
        super(name);
        this.selector = Selector.open();
        this.reactorPool = reactorPool;
    }

    public void register(ServerSocketChannel serverSocketChannel) throws ClosedChannelException {
        if (this == currentThread()) {
            serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        } else {
            regQ.offer(serverSocketChannel);
            this.selector.wakeup();
        }
    }

    @Override
    public void run() {
        while (!interrupted()) {
            try {
                this.selector.select();
                handleRegQ(selector);
                Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
                selectionKeys.forEach(this::handleAcceptKey);
                selectionKeys.clear();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
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
    private void handleRegQ(Selector selector) {
        ServerSocketChannel serverSocketChannel = null;
        while ((serverSocketChannel = this.regQ.poll()) != null) {
            try {
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            } catch (ClosedChannelException e) {
                logger.error(e.getMessage(),e);
            }
        }
    }

    private void handleAcceptKey(SelectionKey key) {
        try {
            if (key.isValid()) {
                if (key.isAcceptable()) {
                    reactorPool.register((SocketChannel) ((ServerSocketChannel) key.channel()).accept().configureBlocking(false));
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
            logger.error(e.getMessage(),e);
        }

    }

    @Override
    public void close() throws IOException {
       this.interrupt();
    }
}
