package io.eyer.proxy.defaults;

import io.eyer.nio.core.net.Connector;
import io.eyer.nio.core.net.ReactorPool;
import io.eyer.nio.core.support.Handler;
import io.eyer.nio.core.support.NioConfig;
import io.eyer.registry.url.URL;
import io.eyer.rpc.codec.KryoSerializer;
import io.eyer.rpc.handler.ClientHandler;
import io.eyer.rpc.invoker.SimpleInvoker;
import io.eyer.rpc.invoker.Invoker;
import io.eyer.rpc.proxy.ProxyClient;
import io.eyer.service.User;
import io.eyer.service.UserService;

/**
 * Created by 青木 on 2015/8/24.
 */
public class ProxyClientTest {
    public static void main(String[] args) throws Exception {
        NioConfig.registered(new KryoSerializer());
        final Handler invocationHandler = new ClientHandler();
        NioConfig.registered(invocationHandler);
        NioConfig.registered(new Connector("connector-1",new ReactorPool(1)));
        NioConfig.initSessionHandler();

        URL url = new URL();
        url.setHost("localhost");
        url.setPort(6060);
        Invoker invoker = new SimpleInvoker(url, 50);

        ProxyClient proxyClient = new ProxyClient(invoker);

        UserService userService = proxyClient.refService(UserService.class);
        User save = userService.save(new User());
        System.out.println(save);
        proxyClient.close();
    }
}
