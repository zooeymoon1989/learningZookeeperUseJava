package com.Watcher;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class WorkerWatcher implements Watcher {

    private final String hostPort;
    private ZooKeeper zk;
    public boolean isLeader =false;
    private final String serverId;

    private final String name;

    private String status;

    public WorkerWatcher(String hostPort,String name) {
        Random random = new Random();
        this.name = name;
        this.hostPort = hostPort;
        this.serverId = Integer.toHexString(random.nextInt());
    }

    public void startZk() throws IOException {
        zk = new ZooKeeper(hostPort,15000,this);
    }


    public void setStatus(String status) {
        this.status = status;
        updateStatus(status);
    }

    synchronized private void updateStatus(String status) {
        if (Objects.equals(status, this.status)) {
            zk.setData(
                    "/workers/" + name,
                    status.getBytes(),
                    -1,
                    (rc, path, ctx, stat) -> {
                        switch (KeeperException.Code.get(rc)){
                            case CONNECTIONLOSS:
                                updateStatus((String) ctx);
                                return;
                        }
                    },
                    status
            );
        }
    }

    public void register() {
        zk.create(
                "/workers/worker-" + serverId,
                "Idle".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL,
                (rc, path, ctx, name) -> {
                    switch (KeeperException.Code.get(rc)){
                        case CONNECTIONLOSS:
                            register();
                            break;
                        case OK:
                            System.out.println("Registered successfully: "+ serverId);
                            break;
                        case NODEEXISTS:
                            System.out.println("Already registered:"+serverId);
                            break;
                        default:
                            System.out.println("something wrong wit "+KeeperException.create(KeeperException.Code.get(rc))+path);
                    }
                },
                null
        );
    }


    @Override
    public void process(WatchedEvent event) {
        System.out.println(event.toString());
    }



}
