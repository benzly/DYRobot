package com.pelucky.danmu.message;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class MessageQueue {
    DelayQueue<DelayRunnable> queue = new DelayQueue<DelayRunnable>();

    // 入队
    public void enqueue(final Message msg, long delayInMilliseconds) {
        if (msg.target == null) {
            throw new IllegalStateException("handler is null");
        }
        DelayRunnable runn = new DelayRunnable() {

            @Override
            public long getDelay(TimeUnit timeUnit) {
                return 0;
            }

            @Override
            public void run() {
                msg.target.dispatchMessage(msg);
            }
        };
        runn.setDelay(delayInMilliseconds);
        queue.add(runn);
    }

    // 出队
    public DelayRunnable next() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public abstract class DelayRunnable implements Delayed, Runnable {

        private long delay;

        public void setDelay(long delayInMilliseconds) {
            this.delay = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delayInMilliseconds, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            DelayRunnable that = (DelayRunnable) o;
            if (this.delay > that.delay) {
                return 1;
            } else if (this.delay < that.delay) {
                return -1;
            }
            return 0;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long result = unit.convert(delay - System.nanoTime(), TimeUnit.NANOSECONDS);
            return result;
        }
    }
}
