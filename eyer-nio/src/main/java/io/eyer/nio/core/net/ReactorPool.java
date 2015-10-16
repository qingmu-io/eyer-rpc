package io.eyer.nio.core.net;

import io.eyer.nio.core.session.Session;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 青木 on 2015/8/17.
 */
public class ReactorPool implements Closeable{
    private Reactor[] rwPools;
    private AtomicInteger index = new AtomicInteger(0);
    private int rwThreads;


    public ReactorPool(int rwThreads) throws IOException {
        this.rwThreads = rwThreads;
        this.rwPools = new Reactor[rwThreads];

        for (int i = 0; i < this.rwPools.length; i++) {
            this.rwPools[i] = new Reactor(String.format("rw event loop index [%d] ", i));
            this.rwPools[i].start();
        }
    }

    public void register(SocketChannel socketChannel) throws ClosedChannelException {
        getNextRWEventLoop().register(socketChannel, SelectionKey.OP_READ);

    }

    public Reactor getNextRWEventLoop() {
        try {
            int i = this.index.getAndIncrement() % this.rwThreads;
            if (i < 0) {
                this.index.set(0);
                i = 0;
            }
            return this.rwPools[i];
        } catch (Exception e) {
            return this.rwPools[0];
        }
    }

    public void register(SocketChannel socketChannel, Session session) throws ClosedChannelException {
        getNextRWEventLoop().registerSess(socketChannel, SelectionKey.OP_READ, session);
    }



    public void close() {
        Arrays.asList(this.rwPools).forEach(rw -> rw.close());
    }


}
