package io.eyer;

import io.eyer.nio.core.net.Connector;
import io.eyer.nio.core.net.ReactorPool;
import io.eyer.nio.core.support.NioConfig;
import io.eyer.rpc.codec.KryoSerializer;
import io.eyer.rpc.handler.ServerHandler;
import io.eyer.rpc.proxy.Client;

import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2015/8/18.
 */
public class TestClient {

    public static void main(String[] args) throws Exception {
        NioConfig.registered(new KryoSerializer());
        final ServerHandler invocationHandler = new ServerHandler();
        NioConfig.registered(invocationHandler);
        NioConfig.initSessionHandler();
        Connector connector = new Connector("connector-1",new ReactorPool(1));

        Client client = new Client(new InetSocketAddress("127.0.0.1",6161));
        System.out.println(client);

    }
}
