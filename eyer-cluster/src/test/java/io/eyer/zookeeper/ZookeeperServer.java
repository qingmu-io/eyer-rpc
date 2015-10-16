package io.eyer.zookeeper;

import io.eyer.nio.core.net.Acceptor;
import io.eyer.nio.core.net.ReactorPool;
import io.eyer.nio.core.support.NioConfig;
import io.eyer.registry.DefaultZookeeperClient;
import io.eyer.registry.ZooKeeperClient;
import io.eyer.rpc.Server;
import io.eyer.rpc.codec.KryoSerializer;
import io.eyer.rpc.handler.ServerHandler;
import io.eyer.service.RemoteUserService;
import io.eyer.service.UserService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLEncoder;

/**
 * Created by 青木 on 2015/8/31.
 */
public class ZookeeperServer {

    public static void main(String[] args) throws IOException {


        NioConfig.registered(new KryoSerializer());
        final ServerHandler handler = new ServerHandler();
        NioConfig.registered(handler);
        NioConfig.initSessionHandler();

        ServerHandler.services.put(UserService.class, new RemoteUserService());
        Server server = new Server(new InetSocketAddress("0.0.0.0", 6161), new Acceptor("acceptor-1", new ReactorPool(1)));

        ZooKeeperClient zooKeeperClient = new DefaultZookeeperClient("192.168.1.66:2181");
        String format = "/eyer/" + URLEncoder.encode(String.format("rpc/%s:%s/Services", "localhost", 6161), "UTF-8");
        zooKeeperClient.create(format, true);
    }


}
