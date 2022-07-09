package com.liwenqiang.Watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class MasterWatcher implements Watcher {

    private final String hostPort;

    public MasterWatcher(String hostPort) {
        this.hostPort = hostPort;
    }


    public ZooKeeper startZk() {
        try {
            return new ZooKeeper(hostPort, 15000, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }
}
