package com.Watcher;

import org.apache.logging.log4j.core.util.JsonUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class AdminClient implements Watcher {

    private ZooKeeper zk;
    String hostPort;

    public AdminClient(String hostPort) {
        this.hostPort = hostPort;
    }

    public void listState() throws InterruptedException, KeeperException {
        Stat stat = new Stat();
        try {
            byte[] masterData = zk.getData("/master", false, stat);
        } catch (KeeperException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Workers:");

        for (String w : zk.getChildren("/workers",false)) {
            byte[] data = zk.getData("/workers" + w, false, null);
            String s = new String(data);
            System.out.println("\t" + w+" : "+s);
        }

        System.out.println("Tasks:");
        for (String t : zk.getChildren("/assign",false)) {
            byte[] data = zk.getData("/assign" + t, false, null);
            String s = new String(data);
            System.out.println("\t" + t);
        }

    }



    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }
}
