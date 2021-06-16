package com.aomygod.tools.toast;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aomygod.tools.R;
import com.aomygod.tools.ToolsLibraray;
import com.aomygod.tools.Utils.ResUtil;
import com.aomygod.tools.Utils.ScreenUtil;

import androidx.annotation.UiThread;


/**
 * 自定义Toast
 */
public final class ToastUtil {

    private static String LAST_CONTENT = "";
    private static long LAST_TIME = 0L;

    /**
     * 显示内容和上一次雷同且5s内不显示
     */
    private static boolean isEnable(String content) {
        if (LAST_CONTENT.equalsIgnoreCase(content) && System.currentTimeMillis() - LAST_TIME < 5000) {
            return false;
        }
        LAST_CONTENT = content;
        LAST_TIME = System.currentTimeMillis();
        return true;
    }


    @UiThread
    public static void showS(String content) {
        showShort(ToolsLibraray.getContext(), content);
    }

    @UiThread
    public static void showS(int strid) {
        showShort(ToolsLibraray.getContext(), ResUtil.getString(strid));
    }

    @UiThread
    private static void showShort(Context context, String content) {
        if (null == context || TextUtils.isEmpty(content)) return;
        if (isEnable(content)) {
            //权限是否开启
            if (NotificationsUtils.isNotificationEnabled(context)) {
                showSystemToast(context, content, Toast.LENGTH_SHORT);
            } else {
                showCustomToast(context, content, Toast.LENGTH_SHORT);
            }
        }
    }


    @UiThread
    public static void showL(String content) {
        show(ToolsLibraray.getContext(), content);
    }

    @UiThread
    public static void showL(int strid) {
        show(ToolsLibraray.getContext(), ResUtil.getString(strid));
    }

    @UiThread
    private static void show(Context context, String content) {
        if (null != context && !TextUtils.isEmpty(content)) {
            if (isEnable(content)) {
                //权限是否开启
                if (NotificationsUtils.isNotificationEnabled(context)) {
                    showSystemToast(context, content, Toast.LENGTH_LONG);
                } else {
                    showCustomToast(context, content, Toast.LENGTH_LONG);
                }
            }
        }
    }

    /**
     * 设置 app 不随着系统字体的调整而变化
     */
    private static void setResources(Context context){
        if(null!=context){
            Resources resources=context.getResources();
            Configuration configuration=new Configuration();
            configuration.setToDefaults();
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
    }


    @UiThread
    private static void showSystemToast(Context context, String content, int duration) {
        setResources(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.tools_layout_custom_toast, null);
        TextView tvContent = (TextView) layout.findViewById(R.id.toast_content);
        tvContent.setText(content);

        Toast toast = new Toast(context);
        //toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }

    @UiThread
    private static void showCustomToast(final Context context, String content, int duration) {
        if (Build.VERSION.SDK_INT > 24) {
            Activity curActivity = ToolsLibraray.getCurActivity();
            if (null != curActivity) {
                setResources(curActivity);
                EToast2.makeText(curActivity, content, duration).show();
            }
        } else {
            setResources(context);
            View layout = LayoutInflater.from(context).inflate(R.layout.tools_layout_custom_toast, null);
            TextView tvContent = (TextView) layout.findViewById(R.id.toast_content);
            tvContent.setText(content);

            ToastCompat toast = new ToastCompat(context.getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, ScreenUtil.getScreenHeight() / 9);
            toast.setDuration(duration);
            toast.setView(layout);
            toast.show();
        }
    }


}
