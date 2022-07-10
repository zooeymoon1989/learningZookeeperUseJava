package com.Watcher;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Random;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class MasterWatcher implements Watcher {

    private final String hostPort;
    private ZooKeeper zk;
    public boolean isLeader =false;
    private final String serverId;

    private final AsyncCallback.StringCallback masterCreateCallback = (rc, path, ctx, name) -> {
        switch (KeeperException.Code.get(rc)) {
            case CONNECTIONLOSS:
                checkMaster();
                return;
            case OK:
                isLeader = true;
                return;
            default:
                isLeader = false;
        }
        System.out.println("I'm "+ (isLeader ? "": "not ") + "the leader");
    };

    private final AsyncCallback.StringCallback createParentCallback = (rc, path, ctx, name) -> {
        switch (KeeperException.Code.get(rc)) {
            case CONNECTIONLOSS:
                break;
            case OK:
                System.out.println("Parent created");
                break;
            case NODEEXISTS:
                System.out.println("Parent already exists");
                break;
            default:
                System.out.println("Something is wrong:"+ KeeperException.create(KeeperException.Code.get(rc))+path);
        }
    };

    private final AsyncCallback.DataCallback masterCheckCallback = (rc, path, ctx, data, stat) -> {
        switch (KeeperException.Code.get(rc)) {
            case CONNECTIONLOSS:
                checkMaster();
                return;
            case NONODE:
                runForMaster();
                return ;
        }
    };


    public MasterWatcher(String hostPort) {
        Random random = new Random();
        this.hostPort = hostPort;
        this.serverId = Integer.toHexString(random.nextInt());
    }


    public void startZk() {
        try {
            zk = new ZooKeeper(hostPort, 15000, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopZk() throws InterruptedException {
        zk.close();
    }


    public void runForMaster() {
        zk.create("/master",serverId.getBytes(),OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL,masterCreateCallback,null);
    }


    public void bootstrap() {
        createParent("/workers",new byte[0]);
        createParent("/assign",new byte[0]);
        createParent("/tasks",new byte[0]);
        createParent("/status",new byte[0]);
    }

    private void createParent(String path, byte[] data) {
        zk.create(
                path,
                data,
                OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,
                createParentCallback,
                data
        );
    }

    private void checkMaster() {
        zk.getData("/master", false,masterCheckCallback , null);
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
    }
}
