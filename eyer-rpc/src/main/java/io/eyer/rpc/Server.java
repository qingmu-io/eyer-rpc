package io.eyer.rpc;

import io.eyer.nio.core.net.Acceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

/**
 * Created by 青木 on 2015/8/18.
 */
public class Server  implements Closeable {
    private static Logger logger = LoggerFactory.getLogger(Server.class);
    private InetSocketAddress bindAddress;
    private Acceptor acceptor;
    ServerSocketChannel serverSocketChannel;

    public Server(InetSocketAddress bindAddress, Acceptor acceptor) throws IOException {
        this.bindAddress = bindAddress;
        if (acceptor.getState() != Thread.State.RUNNABLE) {
            acceptor.start();
        }
        this.acceptor = acceptor;
        serverSocketChannel = (ServerSocketChannel) ServerSocketChannel.open().bind(this.bindAddress).configureBlocking(false);
        acceptor.register(serverSocketChannel);
        logger.info("start server bind in : {}",bindAddress);
    }

    public void close() {
        try {
            serverSocketChannel.close();
            acceptor.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
