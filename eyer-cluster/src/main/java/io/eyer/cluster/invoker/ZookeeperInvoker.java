package io.eyer.cluster.invoker;

import io.eyer.registry.ChildListener;
import io.eyer.registry.DefaultZookeeperClient;
import io.eyer.registry.ZooKeeperClient;
import io.eyer.registry.url.URL;
import io.eyer.rpc.proxy.Client;
import io.eyer.rpc.balance.Balance;
import io.eyer.rpc.invoker.Invoker;
import io.eyer.rpc.proxy.meta.Request;
import io.eyer.rpc.proxy.meta.Response;
import io.eyer.rpc.proxy.PooledClientObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 青木 on 2015/8/26.
 */
public class ZookeeperInvoker implements Invoker, ChildListener {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ZookeeperInvoker.class);
    private String root = "/eyer";
    private ZooKeeperClient zooKeeperClient;
    private GenericKeyedObjectPool<URL, Client> clientGenericObjectPool;
    private Balance balance;
    private List<URL> urls = new ArrayList<>();

    public ZookeeperInvoker(String zkUrl, Balance balance, int workThreads) throws Exception {
        this.zooKeeperClient = new DefaultZookeeperClient(zkUrl);
        List<String> childs = this.zooKeeperClient.getChilds(root);
        final GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
//        config.setMaxTotal(workThreads);
        config.setMaxTotalPerKey(50);
        this.clientGenericObjectPool = new GenericKeyedObjectPool(new PooledClientObjectFactory(), config);
        if (childs == null || childs.isEmpty()) {
            throw new Exception("no service provider");
        }
        this.balance = balance;
        for (String child : childs) {
            URL url = new URL().toURL(child);
            this.urls.add(url);
        }
        this.zooKeeperClient.addChildListener(root, this);
    }

    @Override
    public void notify(List<URL> notifyUrls) {
        //notify urls  = [192.168.1.1,192.168.1.2]
        //native urls = [192.168.1.1,192.168.1.2,192.168.1.3]
        if (notifyUrls.size() < this.urls.size()) {
            final Iterator<URL> iterator = urls.iterator();
            while (iterator.hasNext()) {
                final URL url = iterator.next();
                if (!notifyUrls.contains(url)) {
                    this.clientGenericObjectPool.clear(url);
                    iterator.remove();
                }
            }
        } else if (notifyUrls.size() > this.urls.size()) {
            notifyUrls.forEach(url -> {
                if (!this.urls.contains(url)) {
                    try {
                        this.clientGenericObjectPool.preparePool(url);
                        this.urls.add(url);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            });
        }

    }

    @Override
    public Response invoke(Request request) throws Exception {
        final URL url = this.balance.select(this.urls);
        final Client client = this.clientGenericObjectPool.borrowObject(url);
        try {
            return client.invoker(request);
        } finally {
            this.clientGenericObjectPool.returnObject(url, client);
        }

    }


    @Override
    public void close() throws IOException {
        this.clientGenericObjectPool.close();
    }
}
