package com.aomygod.library.network;


import androidx.core.util.Pools;

import java.util.TreeMap;


/**
 * Created by LiuQiCong
 * date 2020-07-16 16:38
 * version 1.0
 * dsc 描述
 */
public class NetMap extends TreeMap<String, Object> {

    private static final Pools.SynchronizedPool<NetMap> sPool = new Pools.SynchronizedPool<NetMap>(10);
    private boolean hasRecycle=false;

    public static NetMap obtain() {
        NetMap instance = sPool.acquire();
        if(null==instance){
            instance=new NetMap();
        }
        instance.reset();
        return instance;
    }

    public void reset(){
        hasRecycle=false;
    }

    public void recycle() {
        clear();
        if(!hasRecycle){
            hasRecycle=true;
            sPool.release(this);
        }
    }


}
