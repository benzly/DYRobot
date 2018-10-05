package com.pelucky.danmu.message;

public class Looper {
    MessageQueue queue;

    public Looper() {
        queue = new MessageQueue();
    }

    public static void loop() {
        Looper me = myLooper();
        while (true) {
            me.queue.next().run();
        }
    }

    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();

    public static void prepare() {
        // 检查线程中是否已经有一个Looper循环了
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper());
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }
}
