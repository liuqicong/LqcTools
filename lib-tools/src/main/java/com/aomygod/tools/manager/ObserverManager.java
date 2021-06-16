package com.aomygod.tools.manager;

import com.aomygod.tools.entity.Notify;

import java.util.Observable;

/**
 * Created by LiuQiCong
 *
 * @date 2017-07-12 16:22
 * version 1.0
 * dsc 观察者控制中心中心
 */

public class ObserverManager extends Observable {

    private volatile static ObserverManager mInstance;

    public static ObserverManager getInstance() {
        if (mInstance == null) {
            synchronized (ObserverManager.class) {
                if (mInstance == null) {
                    mInstance = new ObserverManager();
                }
            }
        }
        return mInstance;
    }


    public void notifyData(String key, Object data){
        super.setChanged();
        super.notifyObservers(new Notify(key, data));
    }
}
