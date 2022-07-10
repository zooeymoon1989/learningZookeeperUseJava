package com.worker;

import com.Watcher.WorkerWatcher;
import com.master.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class Worker {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final static Properties configProp = new Properties();

    public static void main(String[] args) throws IOException, InterruptedException {
        InputStream in = Main.class.getClassLoader().getResourceAsStream("application.properties");
        logger.info("Reading all properties from the file");
        try {
            configProp.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Random random = new Random();
        WorkerWatcher worker = new WorkerWatcher(configProp.getProperty("hostPort"),Integer.toHexString(random.nextInt()));
        worker.startZk();
        worker.register();
        Thread.sleep(30000);
    }
}
