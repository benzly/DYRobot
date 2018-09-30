package com.pelucky.danmu;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RobotThreadPool {

    public ExecutorService sThreadPool = Executors.newFixedThreadPool(5);


    private static RobotThreadPool sInstance;

    private RobotThreadPool() {

    }

    public static RobotThreadPool getInstance() {
        if (sInstance == null) {
            synchronized (RobotThreadPool.class) {
                if (sInstance == null) {
                    sInstance = new RobotThreadPool();
                }
            }
        }
        return sInstance;
    }
}
