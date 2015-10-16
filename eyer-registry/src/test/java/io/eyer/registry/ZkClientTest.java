package io.eyer.registry;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2015/8/24.
 */
public class ZkClientTest {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {


        DefaultZookeeperClient defaultZookeeperClient = new DefaultZookeeperClient("localhost:2181");
        defaultZookeeperClient.create("/test3",false);


        defaultZookeeperClient.create("/test3/"+ URLEncoder.encode("rpc/192.168.1.1:6161/com.eyer.service.UserService","UTF-8"),true);

        defaultZookeeperClient.create("/test3/"+ URLEncoder.encode("rpc/192.168.1.1:6162/com.eyer.service.UserService","UTF-8"),true);

        defaultZookeeperClient.create("/test3/"+ URLEncoder.encode("rpc/192.168.1.1:6163/com.eyer.service.UserService","UTF-8"),true);
        System.in.read();
    }
}
