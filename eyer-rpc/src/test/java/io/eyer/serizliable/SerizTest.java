package io.eyer.serizliable;

import io.eyer.nio.core.buffer.IoBuffer;
import io.eyer.rpc.codec.KryoSerializer;
import io.eyer.rpc.proxy.meta.Request;
import org.junit.Test;

/**
 * Created by Administrator on 2015/9/2.
 */
public class SerizTest {

    @Test
    public void testKry(){
        final KryoSerializer kryoSerializer = new KryoSerializer();
        final IoBuffer encode = kryoSerializer.encode(new Request());
        System.out.println(encode);
    }
}
