package io.eyer.rpc.invoker;

import io.eyer.registry.url.URL;
import io.eyer.rpc.proxy.Client;
import io.eyer.rpc.proxy.meta.Request;
import io.eyer.rpc.proxy.meta.Response;
import io.eyer.rpc.proxy.PooledClientObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by 青木 on 2015/8/25.
 */
public class SimpleInvoker implements Invoker {
    private static Logger logger = LoggerFactory.getLogger(SimpleInvoker.class);
    private int workThreads;
    private static final ScheduledExecutorService heartbeator = Executors.newSingleThreadScheduledExecutor();
    private int heartbeatInterval = 60; //60s
    private URL url;
    private GenericKeyedObjectPool<URL, Client> clientGenericObjectPool;


    public SimpleInvoker(URL url, int workThreads) throws IOException, InterruptedException {
        this.url = url;
        this.workThreads = workThreads;
        final GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMaxTotal(workThreads);
        config.setMaxTotalPerKey(workThreads);
       this.clientGenericObjectPool =  new GenericKeyedObjectPool(new PooledClientObjectFactory(), config);
    }


    @Override
    public Response invoke(Request request) throws Exception {
        Client client = this.clientGenericObjectPool.borrowObject(url);
        Response response = null;
        try {
            response = client.invoker(request);
        } finally {
            this.clientGenericObjectPool.returnObject(url, client);
        }
        return response;
    }

    @Override
    public void close() throws IOException {
        clientGenericObjectPool.setTestOnBorrow(true);
    }
}
