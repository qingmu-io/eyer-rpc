package io.eyer.serizliable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class KryoPoolTest {

    private static KryoFactory factory = new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            // configure kryo
            return kryo;
        }
    };

    @Parameters
    public static Collection<Object[]> data() {
        final KryoPool.Builder builder = new KryoPool.Builder(factory);
        final KryoPool build = builder.build();
        return Arrays.asList(new Object[][]{
                {builder},
                {new KryoPool.Builder(factory).softReferences()}
        });
    }

    private KryoPool pool;

    public KryoPoolTest(KryoPool.Builder builder) {
        pool = builder.build();
    }


    @Test
    public void getShouldReturnAvailableInstance() {
        Kryo kryo = pool.borrow();
        pool.release(kryo);
        assertTrue(kryo == pool.borrow());
    }


    @Test(expected = IllegalArgumentException.class)
    public void runWithKryoShouldRethrowException() {
        String value = pool.run(new KryoCallback<String>() {
            @Override
            public String execute(Kryo kryo) {
                throw new IllegalArgumentException();
            }
        });
    }

}
