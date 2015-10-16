package io.eyer.nio.core.serializer;

import io.eyer.nio.core.buffer.IoBuffer;

/**
 * Created by 青木 on 2015/8/18.
 */
public interface Serializer {

    IoBuffer encode(Object obj);

    Object decode(byte[] bytes);
}
