package io.eyer;

import io.eyer.nio.core.net.Acceptor;
import io.eyer.nio.core.net.ReactorPool;
import io.eyer.nio.core.support.NioConfig;
import io.eyer.rpc.Server;
import io.eyer.rpc.codec.KryoSerializer;
import io.eyer.rpc.handler.ServerHandler;
import io.eyer.service.RemoteUserService;
import io.eyer.service.UserService;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2015/8/18.
 */
public class TestServer {
    public static void main(String[] args) throws IOException {

        NioConfig.registered(new KryoSerializer());
        final ServerHandler handler = new ServerHandler();
        NioConfig.registered(handler);
        NioConfig.initSessionHandler();
        //需要导出的服务接口 以及对应的实现类
        ServerHandler.services.put(UserService.class, new RemoteUserService());
        Server server = new Server(new InetSocketAddress("0.0.0.0",6161),new Acceptor("acceptor-1",new ReactorPool(1)));

    }
}
