package io.eyer.registry;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 青木 on 2015/8/24.
 */
public class DefaultZookeeperClient implements ZooKeeperClient {

    private static Logger logger = LoggerFactory.getLogger(DefaultZookeeperClient.class);
    public ZooKeeper zooKeeper;
    private String zkUrl;


    private ConcurrentHashMap<String, ZkWatcher> watchers = new ConcurrentHashMap<>();

    public DefaultZookeeperClient(String zkUrl) throws IOException {
        this.zkUrl = zkUrl;
        this.zooKeeper = new ZooKeeper(zkUrl, 10000, event -> {});
    }

    @Override
    public void create(String path, boolean ephemeral) {
        try {
            if (this.zooKeeper.exists("/eyer", false) == null) this.zooKeeper.create("/eyer", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            if (this.zooKeeper.exists(path, false) != null) return;
            if (ephemeral) {
                this.zooKeeper.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            } else {
                this.zooKeeper.create(path, path.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void delete(String path) {
        try {
            this.zooKeeper.delete(path, -1);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } catch (KeeperException e) {
            logger.error(e.getMessage(), e);
        }
    }


    @Override
    public void addChildListener(String path, ChildListener listener) {
        this.create(path, false);
        watchers.putIfAbsent(path, new ZkWatcher(this.zkUrl, path, listener));
    }

    @Override
    public List<String> getChilds(String path) throws Exception {
        this.create(path, false);
        return this.zooKeeper.getChildren(path, false);
    }



}
