package com.frannyzhao.mqttlib.jobqueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhaofengyi on 4/12/18.
 */

public class JobManager {
    private final static ExecutorService singleThread = Executors.newSingleThreadExecutor();

    public static void post(Runnable runnable) {
        if (runnable != null) {
            singleThread.execute(runnable);
        }
    }
}
