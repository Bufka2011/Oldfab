package com.wuest.prefab.Util;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Minecraft 1.7.10 handles network messages on the netty thread. These queues are used to run the message logic on
 * the main client/server thread. The queues are processed in the tick events.
 */
public class MainThreadTasks {

    private static final ConcurrentLinkedQueue<Runnable> serverTasks = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<Runnable> clientTasks = new ConcurrentLinkedQueue<>();

    public static void addServerTask(Runnable task) {
        serverTasks.add(task);
    }

    public static void addClientTask(Runnable task) {
        clientTasks.add(task);
    }

    public static void runServerTasks() {
        Runnable task;

        while ((task = serverTasks.poll()) != null) {
            task.run();
        }
    }

    public static void runClientTasks() {
        Runnable task;

        while ((task = clientTasks.poll()) != null) {
            task.run();
        }
    }
}
