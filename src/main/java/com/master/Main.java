package com.master;

import com.Watcher.MasterWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final static Properties configProp = new Properties();
    public static void main(String[] args) throws InterruptedException {

        //Private constructor to restrict new instances
        InputStream in = Main.class.getClassLoader().getResourceAsStream("application.properties");
        logger.info("Reading all properties from the file");
        try {
            configProp.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MasterWatcher zk = new MasterWatcher(configProp.getProperty("hostPort"));
        zk.startZk();
        zk.runForMaster();
        if (zk.isLeader) {
            logger.info("I'm the leader");
            Thread.sleep(60000);
        }else {
            logger.info("someone else is the leader");
        }
        zk.stopZk();
    }
}