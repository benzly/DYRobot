package com.pelucky.danmu.message;

public class Message {

    public int what;
    public Object obj1;
    public Object obj2;
    public Handler target;
    public Runnable callback;

    public Message(int what, Object obj1, Object obj2) {
        super();
        this.what = what;
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public Message(int what) {
        super();
        this.what = what;
    }

    public Message(Runnable run) {
        super();
        this.callback = run;
    }
}
