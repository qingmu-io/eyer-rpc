package io.eyer.nio.core.buffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BufferPool {
    private static Logger log = LoggerFactory.getLogger(BufferPool.class);// 日志记录器

    private static int maxBufferPoolSize = 1000;// 默认的直接缓冲区池上限大小1000
    private static int minBufferPoolSize = 1000;// 默认的直接缓冲区池下限大小1000
    private static int writeBufferSize = 8;// 响应缓冲区大小默认为4k

    private static BufferPool bufferPool = new BufferPool();// BufferPool的单实例

    private AtomicInteger usableCount = new AtomicInteger();// 可用缓冲区的数量
    private AtomicInteger createCount = new AtomicInteger();// 已创建了缓冲区的数量
    private ConcurrentLinkedQueue<ByteBuffer> queue = new ConcurrentLinkedQueue<ByteBuffer>();// 保存直接缓存的队列

    static {
        // 设置缓冲区池上限大小
        Integer maxSize = 10000;
        if (maxSize != null) {
            maxBufferPoolSize = maxSize;
        }

        // 设置缓冲区池下限大小
        Integer minSize = 100;
        if (minSize != null) {
            minBufferPoolSize = minSize;
        }

        // 设置响应缓冲区大小
        Integer bufferSize = 1000;
        if (bufferSize != null) {
            writeBufferSize = bufferSize;
        }
    }

    private BufferPool() {
        // 预先创建直接缓冲区
        for (int i = 0; i < minBufferPoolSize; ++i) {
            ByteBuffer bb = ByteBuffer.allocate(writeBufferSize * 1024);
            this.queue.add(bb);
        }

        // 设置可用的缓冲区和已创建的缓冲区数量
        this.usableCount.set(minBufferPoolSize);
        this.createCount.set(minBufferPoolSize);
    }


    public ByteBuffer getBuffer() {
        ByteBuffer bb = this.queue.poll();

        if (bb == null) {// 如果缓冲区不够则创建新的缓冲区
            bb = ByteBuffer.allocate(writeBufferSize * 1024);
            this.createCount.incrementAndGet();
        } else {
            this.usableCount.decrementAndGet();
        }

        return bb;
    }


    public void releaseBuffer(ByteBuffer bb) {
        if (this.createCount.intValue() > maxBufferPoolSize && (this.usableCount.intValue() > (this.createCount.intValue() / 2))) {
            bb = null;
            this.createCount.decrementAndGet();
        } else {
            this.queue.add(bb);
            this.usableCount.incrementAndGet();
        }
    }

    public static BufferPool getInstance() {
        return bufferPool;
    }
}
