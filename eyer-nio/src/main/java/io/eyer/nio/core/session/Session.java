package io.eyer.nio.core.session;

import io.eyer.nio.core.buffer.IoBuffer;
import io.eyer.nio.core.serializer.Serializer;
import io.eyer.nio.core.support.NioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

/**
 * Created by 青木 on 2015/8/17.
 */
public class Session {
    private static Logger logger = LoggerFactory.getLogger(Session.class);

    public enum Status {
        NEW, OPEN, CLOSED, ERROR
    }

    public Session() {
        this.id = UUID.randomUUID().toString();
        this.status = Status.NEW;
        logger.info("created session success, session id :[{}],session state : [{}]", this.id, this.status);
    }

    protected String id;
    protected SocketChannel socketChannel;
    protected SelectionKey key;
    protected IoBuffer readBuffer;
    protected Queue<ByteBuffer> writeBufferQueue = new ConcurrentLinkedQueue<>();
    protected Date lastInvokerTime;
    private CountDownLatch latch = new CountDownLatch(1);
    private CountDownLatch regLeach = new CountDownLatch(1);
    private Serializer serializer = NioConfig.getSerializer();
    public Status status = Status.CLOSED;

    public Session(SocketChannel socketChannel, SelectionKey key, Date lastInvokerTime) {
        this.socketChannel = socketChannel;
        this.key = key;
        this.lastInvokerTime = lastInvokerTime;
    }

    public void updateLastTime() {
        this.lastInvokerTime = new Date(System.currentTimeMillis());
    }

    public void write(ByteBuffer buffer) {
        this.writeBufferQueue.offer(buffer);
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        key.selector().wakeup();
    }


    public void write(Object obj) {
        this.write(serializer.encode(obj).buf());
    }


    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public IoBuffer getReadBuffer() {
        return readBuffer;
    }

    public void setReadBuffer(IoBuffer readBuffer) {
        this.readBuffer = readBuffer;
    }

    public Queue<ByteBuffer> getWriteBufferQueue() {
        return writeBufferQueue;
    }

    public void setWriteBufferQueue(Queue<ByteBuffer> writeBufferQueue) {
        this.writeBufferQueue = writeBufferQueue;
    }

    public Date getLastInvokerTime() {
        return lastInvokerTime;
    }

    public void setLastInvokerTime(Date lastInvokerTime) {
        this.lastInvokerTime = lastInvokerTime;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public CountDownLatch getRegLeach() {
        return regLeach;
    }

    public void setRegLeach(CountDownLatch regLeach) {
        this.regLeach = regLeach;
    }

    public void close() throws IOException {
        this.key.channel();
        this.socketChannel.close();
        this.status = Status.CLOSED;
    }

    public boolean isError() {
        return this.status == Status.ERROR;
    }

    public boolean isClose() {
        return this.status == Status.CLOSED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        return !(id != null ? !id.equals(session.id) : session.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
