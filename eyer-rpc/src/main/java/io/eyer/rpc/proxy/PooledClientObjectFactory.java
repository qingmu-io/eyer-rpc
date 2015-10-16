package io.eyer.rpc.proxy;

import io.eyer.registry.url.URL;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by Administrator on 2015/8/31.
 */
public class PooledClientObjectFactory extends BaseKeyedPooledObjectFactory<URL, Client> {


    @Override
    public Client create(URL url) throws Exception {
        return new Client(url.toAddress());
    }

    @Override
    public PooledObject<Client> wrap(Client client) {
        return new DefaultPooledObject<Client>(client);
    }

    @Override
    public void destroyObject(URL key, PooledObject<Client> p) throws Exception {
        p.getObject().close();
        super.destroyObject(key, p);
    }
}
