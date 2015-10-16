package io.eyer.registry;

import io.eyer.registry.url.URL;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 青木 on 2015/8/24.
 */
public class ZkWatcher implements Watcher {

    private static Logger logger = LoggerFactory.getLogger(ZkWatcher.class);

    private String zkUrl;
    private int timeout;
    private ZooKeeper zooKeeper;
    private String path;
    private ChildListener zkListener;

    public ZkWatcher(String zkUrl,String path, ChildListener zkListener) {
        this(zkUrl, 1000, path, zkListener);
    }

    public ZkWatcher(String zkUrl, int timeout, String path, ChildListener zkListener)  {
        this.zkUrl = zkUrl;
        this.timeout = timeout;
        this.path = path;
        this.zkListener = zkListener;
        try {
            this.zooKeeper = new ZooKeeper(zkUrl, timeout, this);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            List<String> children = zooKeeper.getChildren(path, this);
            List<URL> urls = new ArrayList<>(children.size());
            children.forEach(url -> {
                urls.add(new URL().toURL(url));
            });
            if (zkListener != null) zkListener.notify(urls);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
