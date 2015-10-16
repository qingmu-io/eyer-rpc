package io.eyer.registry;

import java.io.IOException;

/**
 * Created by Administrator on 2015/8/24.
 */
public class Lis {
    public static void main(String[] args) throws IOException, InterruptedException {
//        new ZkWatcher("localhost:2181",1000,"/test1",System.out::print);
        new DefaultZookeeperClient("localhost:2181").addChildListener("/test3",urls -> System.out.println(urls));
        while (true) Thread.sleep(Long.MAX_VALUE);
    }
}
