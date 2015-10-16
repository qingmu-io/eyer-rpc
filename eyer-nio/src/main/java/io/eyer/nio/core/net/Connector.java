package io.eyer.nio.core.net;

import io.eyer.nio.core.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Administrator on 2015/8/17.
 */
public class Connector extends Thread implements Closeable {
    private static Logger logger = LoggerFactory.getLogger(Connector.class);
    private Selector selector;
    private ReactorPool reactorPool;
    private Queue<Session> regQ = new ConcurrentLinkedQueue<>();


    public Connector(String name, ReactorPool reactorPool) throws IOException {
        super(name);
        this.reactorPool = reactorPool;
        this.selector = Selector.open();
    }

    @Override
    public void run() {
        while (!interrupted()) {
            try {
                this.selector.select();
                this.handleReg(selector);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                selectionKeys.forEach(this::handleConnectEventKey);
                selectionKeys.clear();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        try {
            close0(selector, reactorPool);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void close0(Selector selector, ReactorPool reactorPool) throws IOException {
        if(selector.isOpen()){
            this.selector.select(1L);
            this.selector.selectedKeys().forEach(k->{try{k.cancel();k.channel().close();}catch (Exception e){logger.error(e.getMessage(),e);}});
        }
        reactorPool.close();
    }

    private void handleConnectEventKey(SelectionKey key) {
        try {
            if (key.isValid()) {
                if (key.isConnectable()) {
                    Session session = (Session) key.attachment();
                    while (!session.getSocketChannel().finishConnect()) {
                        System.out.println("check finish connection");
                    }
                    key.interestOps(0);
                    key.cancel();
                    reactorPool.register(session.getSocketChannel(), session);
                } else {
                    key.cancel();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    public Session connect(InetSocketAddress remoteAddress) throws IOException, InterruptedException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        Session session = new Session(socketChannel, null, new Date());
        this.regQ.offer(session);
        this.selector.wakeup();
        session.getRegLeach().await();

        socketChannel.connect(remoteAddress);
        session.getLatch().await();


        return session;
    }

    public ReactorPool getReactorPool() {
        return reactorPool;
    }

    public void setReactorPool(ReactorPool reactorPool) {
        this.reactorPool = reactorPool;
    }

    private void handleReg(Selector selector) {
        Session session = null;
        while ((session = this.regQ.poll()) != null) {
            try {
                SelectionKey key = session.getSocketChannel().register(selector, SelectionKey.OP_CONNECT, session);
                session.setKey(key);
                session.getRegLeach().countDown();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


    @Override
    public void close() throws IOException {
        this.interrupt();
    }
}
