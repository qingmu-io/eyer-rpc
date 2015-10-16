package io.eyer.registry;

import org.apache.zookeeper.KeeperException;

import java.util.List;

/**
 * Created by 青木 on 2015/8/24.
 */
public interface ZooKeeperClient {

    void create(String path, boolean ephemeral);

    void delete(String path);

    void addChildListener(String path, ChildListener listener);


    List<String> getChilds(String root) throws Exception;
}
