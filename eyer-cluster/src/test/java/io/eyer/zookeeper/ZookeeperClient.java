package io.eyer.zookeeper;

import io.eyer.cluster.balance.PollingBalance;
import io.eyer.cluster.invoker.ZookeeperInvoker;
import io.eyer.nio.core.net.Connector;
import io.eyer.nio.core.net.ReactorPool;
import io.eyer.nio.core.support.Handler;
import io.eyer.nio.core.support.NioConfig;
import io.eyer.rpc.codec.KryoSerializer;
import io.eyer.rpc.handler.ClientHandler;
import io.eyer.rpc.invoker.Invoker;
import io.eyer.rpc.proxy.ProxyClient;
import io.eyer.service.User;
import io.eyer.service.UserService;

/**
 * Created by 青木 on 2015/8/31.
 */
public class ZookeeperClient {
    public static void main(String[] args) throws Exception {
        NioConfig.registered(new KryoSerializer());
        final Handler invocationHandler = new ClientHandler();
        NioConfig.registered(invocationHandler);
        NioConfig.registered(new Connector("Connector-ZookeeperClient-1",new ReactorPool(1)));
        NioConfig.initSessionHandler();

       String zkUrl = "192.168.1.66:2181";

        Invoker invoker = new ZookeeperInvoker(zkUrl,new PollingBalance(),50);

        ProxyClient proxyClient = new ProxyClient(invoker);

        UserService userService = proxyClient.refService(UserService.class);
        User save = userService.save(new User());
        System.out.println(save);
        proxyClient.close();
    }
}
