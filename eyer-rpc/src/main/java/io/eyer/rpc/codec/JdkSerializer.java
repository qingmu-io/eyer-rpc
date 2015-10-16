package io.eyer.rpc.codec;

import io.eyer.nio.core.buffer.IoBuffer;
import io.eyer.nio.core.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


/**
 * Created by 青木 on 2015/8/18.
 */
public class JdkSerializer implements Serializer {
    private static Logger logger = LoggerFactory.getLogger(JdkSerializer.class);

    @Override
    public IoBuffer encode(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            final byte[] bytes = baos.toByteArray();
            return IoBuffer.allocate(4+bytes.length).put(bytes.length).put(bytes).flip();
        } catch (IOException e) {
           logger.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @Override
    public Object decode(byte[] bytes) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e.getMessage(),e);
        }
    }
}
