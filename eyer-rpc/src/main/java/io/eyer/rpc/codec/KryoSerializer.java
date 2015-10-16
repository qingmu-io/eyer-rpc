package io.eyer.rpc.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.UnsafeInput;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import io.eyer.nio.core.buffer.IoBuffer;
import io.eyer.nio.core.serializer.Serializer;
import io.eyer.service.User;

/**
 * Created by Administrator on 2015/8/31.
 */
public class KryoSerializer implements Serializer {
    private static final KryoPool kryoPool = new KryoPool.Builder(new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.register(User.class);
            return kryo;
        }
    }).build();

    @Override
    public IoBuffer encode(Object obj) {
        final Kryo kryo = kryoPool.borrow();
        try (UnsafeOutput output = new UnsafeOutput(new byte[1024*4])) {
            kryo.writeClassAndObject(output, obj);
            final byte[] bytes = output.toBytes();
            return IoBuffer.allocate(4 + bytes.length).put(bytes.length).put(bytes).flip();
        } finally {
            kryoPool.release(kryo);
        }

    }

    @Override
    public Object decode(byte[] bytes) {
        final Kryo kryo = kryoPool.borrow();
        try (UnsafeInput input = new UnsafeInput(bytes)) {
           return kryo.readClassAndObject(input);
        } finally {
            kryoPool.release(kryo);
        }
    }


}
