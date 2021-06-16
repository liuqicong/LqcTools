package com.aomygod.library.network;

import org.json.JSONObject;

import androidx.core.util.Pools;


/**
 * Created by LiuQiCong
 * date 2020-07-20 14:13
 * version 1.0
 * dsc 描述
 */
public class NetJson extends JSONObject {

    private static final Pools.SynchronizedPool<NetJson> sPool = new Pools.SynchronizedPool<NetJson>(4);
    private boolean hasRecycle=false;
    public static NetJson obtain() {
        NetJson instance = sPool.acquire();
        if(null==instance){
            instance=new NetJson();
        }
        instance.reset();
        return instance;
    }

    public void reset(){
        hasRecycle=false;
    }

    public void recycle() {
        while (keys().hasNext()){
            String key=keys().next();
            this.remove(key);
        }

        if(!hasRecycle){
            hasRecycle=true;
            sPool.release(this);
        }
    }

    //=======================================================================================
    public NetJson put(String name, Object value){
        if (value != null) {
            try {
                super.put(name, value);
            } catch (Exception e) {}
        }
        return this;
    }

    public NetJson put(String name, int value){
        try {
            super.put(name, value);
        } catch (Exception e) {}
        return this;
    }


    public NetJson put(String name, long value){
        try {
            super.put(name, value);
        } catch (Exception e) {}
        return this;
    }


    public NetJson put(String name, boolean value){
        try {
            super.put(name, value);
        } catch (Exception e) {}
        return this;
    }


}
