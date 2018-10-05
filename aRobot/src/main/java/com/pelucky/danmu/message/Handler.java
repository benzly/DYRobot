package com.pelucky.danmu.message;

public class Handler {
    Looper mLooper;
    MessageQueue mQueue;
    private CallBack mCallback;

    public Handler(Looper mLooper, CallBack callBack) {
        super();
        this.mLooper = mLooper;
        this.mCallback = callBack;
        mQueue = mLooper.queue;
    }

    public void post(Runnable r) {
        Message msg = new Message(r);
        if (msg.target == null) {
            msg.target = this;
        }
        mQueue.enqueue(msg, 0);
    }

    public void postDelay(Runnable r, long delayMillis) {
        Message msg = new Message(r);
        if (msg.target == null) {
            msg.target = this;
        }
        mQueue.enqueue(msg, delayMillis);
    }

    public void sendMessage(Message msg) {
        if (msg.target == null) {
            msg.target = this;
        }
        mQueue.enqueue(msg, 0);
    }

    public void handleMessage(Message message) {

    }

    private static void handleCallback(Message message) {
        message.callback.run();
    }

    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }

    public interface CallBack {
        boolean handleMessage(Message msg);
    }
}
