package com.Watcher;

import org.apache.zookeeper.*;

import java.io.IOException;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class ClientWatcher implements Watcher {

    private final String hostPort;
    private ZooKeeper zk;

    public ClientWatcher(String hostPort) {
        this.hostPort = hostPort;
    }

    public void startZk() throws IOException {
        this.zk = new ZooKeeper(hostPort,15000,this);
    }


    public String queueCommand(String command) {
        while (true) {
            try {
                String name = zk.create("/tasks/task-",
                        command.getBytes(),
                        OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL_SEQUENTIAL
                );
                return name;
            } catch (KeeperException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }
}
