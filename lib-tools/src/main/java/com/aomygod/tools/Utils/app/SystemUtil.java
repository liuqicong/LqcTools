package com.aomygod.tools.Utils.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 当前程序相关工具类
 */
public final class SystemUtil {

    private static int CpuCoresNum = 0;

    private static class CpuFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            // TODO Auto-generated method stub
            if (Pattern.matches("cpu[0-9]", pathname.getName()))
                return true;
            return false;
        }
    }

    public static int getCpuCoresNum() {
        if (CpuCoresNum == 0) {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            CpuCoresNum = files.length;
        }
        return CpuCoresNum;
    }


    public static String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader br = new BufferedReader(new FileReader(file));
            return br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> rapis = am.getRunningAppProcesses();
        if (rapis == null) {
            rapis = am.getRunningAppProcesses();
        }
        if (rapis != null) {
            for (ActivityManager.RunningAppProcessInfo rapi : rapis) {
                if (rapi.pid == android.os.Process.myPid()) {
                    return rapi.processName;
                }
            }
        }
        return null;
    }

    public static boolean isProcessFront(Context context, String processName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> rapis = am.getRunningAppProcesses();
        if (rapis == null) {
            rapis = am.getRunningAppProcesses();
        }
        if (rapis != null) {
            for (ActivityManager.RunningAppProcessInfo rapi : rapis) {
                if (processName.equals(rapi.processName)) {
                    if (rapi.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static int getCurrentVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static String getCurrentVersionName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取当前Activity展示的高度
     *
     * @param activity
     * @return
     */
    public static int getActivityDisplayHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 判断网络是否畅通
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                int l = info.length;
                for (int i = 0; i < l; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 打开网络设置
     */
    public static void openNetSetting(Context mContext) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        // 判断手机系统的版本 即API大于10 就是3.0或以上版本
        // if (android.os.Build.VERSION.SDK_INT > 10) {
        // intent = new
        // Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        // } else {
        // intent = new Intent();
        // ComponentName component = new
        // ComponentName("com.android.settings","com.android.settings.WirelessSettings");
        // intent.setComponent(component);
        // intent.setAction("android.intent.action.VIEW");
        // }
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // intent.setAction(Settings.ACTION_WIFI_SETTINGS); //直接进入手机中的wifi网络设置界面
        mContext.startActivity(intent);
    }

    public static FingerprintManager getFingerprintManager(Context context) {
        FingerprintManager fingerprintManager = null;
        try {
            fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        } catch (Throwable e) {

        }
        return fingerprintManager;
    }

    /**
     * 如果没有设置指纹进入设置界面设置指纹
     */
    private static final String ACTION_SETTING = "android.settings.SETTINGS";

    public static void openFingerPrintSettingPage(Context context) {
        Intent intent = new Intent(ACTION_SETTING);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }


    /**
     * 检测是否已安装应用程序
     */
    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (PackageInfo packageInfo : pinfo) {
                if (packageName.equals(packageInfo.packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
