package io.eyer.nio.core.session;

import io.eyer.nio.core.buffer.BufferPool;
import io.eyer.nio.core.buffer.IoBuffer;
import io.eyer.nio.core.support.NioConfig;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * Created by 青木 on 2015/8/17.
 */
public class SessionHandler {


    public synchronized void handleRead(Session session) throws IOException {
        if (session.readBuffer == null) session.readBuffer = IoBuffer.allocate(1024 * 8);
        ByteBuffer buf = BufferPool.getInstance().getBuffer();  //ByteBuffer.allocate(1024 * 4);//
        while (session.socketChannel.read(buf) > 0) {
            session.readBuffer.put(buf.array(), 0, buf.remaining());
            buf.clear();
        }
       BufferPool.getInstance().releaseBuffer(buf);
        IoBuffer tempBuf = session.readBuffer.flip();
        while (true) {
            tempBuf.mark();
            if(tempBuf.remaining() <= 4) {
                tempBuf.reset();
                this.restReadBuffer(tempBuf, session);
                return;
            }

            int len = tempBuf.getInt();
            if(len > tempBuf.remaining() || len <= 0) {
                tempBuf.reset();
                this.restReadBuffer(tempBuf, session);
                return;
            }

            byte[] body = new byte[len];
            tempBuf.get(body);
            Object obj = this.handlePacket(body);
            if(obj == null) {
                tempBuf.reset();
                this.restReadBuffer(tempBuf, session);
                return;
            }

            NioConfig.getHandler().onPacket(session, obj);
        }
    }

    public void handleWrite(Session session) throws IOException {
        synchronized (session.writeBufferQueue) {
            while (true) {
                ByteBuffer buffer = session.writeBufferQueue.peek();
                if (buffer == null) {
                    session.key.interestOps(session.key.interestOps() & ~SelectionKey.OP_WRITE);
                    return;
                }
                int write = session.socketChannel.write(buffer);
                if(write == 0 && buffer.remaining() > 0) {
                    return;
                }

                if(buffer.remaining() != 0) {
                    return;
                }

                session.writeBufferQueue.remove();
            }
        }
    }

    private Object handlePacket(byte[] body) throws IOException {
        try {
            return NioConfig.getSerializer().decode(body);
        } catch (RuntimeException e) {
            NioConfig.getHandler().onCodecException("packect decode error", e);
            throw new IOException("packect parse error",e);
        }
    }

    private void restReadBuffer(IoBuffer tempBuf, Session session) {
        if (tempBuf != null && tempBuf.remaining() > 0) {
            session.readBuffer = IoBuffer.wrap(tempBuf.array());
        } else {
            session.readBuffer = null;
        }
    }


}
