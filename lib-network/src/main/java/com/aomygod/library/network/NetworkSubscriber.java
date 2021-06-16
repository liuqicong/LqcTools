package com.aomygod.library.network;

public abstract class NetworkSubscriber<T extends Object> {

    private Class<T> cls;

    public Class getCls(){
        return cls;
    }

    public NetworkSubscriber(Class<T> cls) {
        this.cls = cls;
    }



    public abstract void onNext(T o);

    public abstract void onError(Throwable t);

    public abstract void onComplete();
}
