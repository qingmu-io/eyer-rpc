package io.eyer.cluster.balance;

import io.eyer.registry.url.URL;
import io.eyer.rpc.balance.Balance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2015/8/25.
 */
public class PollingBalance implements Balance {

    private AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public URL select(List<URL> urls) {
        int i = this.atomicInteger.getAndIncrement() % urls.size();
        if (i < 0) {
            this.atomicInteger.set(0);
            i = 0;
        }
        return urls.get(i);
    }
}
