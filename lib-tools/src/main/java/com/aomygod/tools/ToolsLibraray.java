package com.aomygod.tools;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;

import com.aomygod.tools.Utils.PreferencesUtil;
import com.aomygod.tools.Utils.ResUtil;
import com.aomygod.tools.Utils.ScreenUtil;

import java.lang.ref.WeakReference;

/**
 * Created by LiuQiCong
 *
 * @date 2017-06-07 15:24
 * version 1.0
 * dsc ToolsLibraray模块库初始化
 */

public final class ToolsLibraray {

    private volatile static ToolsLibraray mInstance;
    private static Context mContext;
    private static WeakReference<Activity> activityWeakReference;

    public static ToolsLibraray init(Application application) {
        if (mInstance == null) {
            synchronized (ToolsLibraray.class) {
                if (mInstance == null) {
                    mInstance = new ToolsLibraray(application);
                }
            }
        }
        return mInstance;
    }

    private ToolsLibraray(Application application){
        mContext=application.getApplicationContext();

        //注册activity监听
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks);

        ResUtil.init(mContext);
        ScreenUtil.init(mContext);
        PreferencesUtil.init(mContext);

    }


    private ActivityLifecycleCallbacks lifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activityWeakReference=new WeakReference<>(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {}

        @Override
        public void onActivityStopped(Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

        @Override
        public void onActivityDestroyed(Activity activity) {}

    };


    public static Context getContext(){
        return mContext;
    }

    public static Activity getCurActivity(){
        return activityWeakReference.get();
    }

}
