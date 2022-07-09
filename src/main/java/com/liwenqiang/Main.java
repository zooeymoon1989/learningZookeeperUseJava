package com.liwenqiang;

import com.liwenqiang.Watcher.MasterWatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    private final static Properties configProp = new Properties();
    public static void main(String[] args) throws InterruptedException {

        //Private constructor to restrict new instances
        InputStream in = Main.class.getClassLoader().getResourceAsStream("application.properties");
        System.out.println("Reading all properties from the file");
        try {
            configProp.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MasterWatcher zk = new MasterWatcher(configProp.getProperty("hostPort"));
        Thread.sleep(Integer.parseInt(configProp.getProperty("threadSleep")));
    }
}