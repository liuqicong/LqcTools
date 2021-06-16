package com.aomygod.tools.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import androidx.core.app.ActivityCompat;

/**
 * Created by LiuQiCong
 *
 * @date 2017-06-07 17:21
 * version 1.0
 * dsc 权限工具类
 */

public final class PermissionUtil {

    public static void autoGotoPermission(Context context) {
        boolean result = false;
        if (isMIUI()) {
            result = gotoMiuiPermission(context);
        }
//        else if (gotoHuaweiPermission(context)) {
//            //Utils.e("---------------Huawei------------------");
//            ToastUtil.show(context, "若收不到推送消息,请允许通知类相关权限");
//        } else if(gotoMeizuPermission(context)){
//            //Utils.e("--------------Meizu-------------------");
//            ToastUtil.show(context,"若收不到推送消息,请允许通知类相关权限");
//        }
        if (!result) {
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }


    /**
     * 跳转到miui的权限管理页面
     */
    private static boolean gotoMiuiPermission(Context context) {
        try {
            Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(intent);
            } catch (Exception e1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否是MIUI
     */
    private static boolean isMIUI() {
        String device = Build.MANUFACTURER;
        if (device.equals("Xiaomi")) {
            try {
                Properties prop = new Properties();
                prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
                return prop.getProperty("ro.miui.ui.version.code", null) != null
                        || prop.getProperty("ro.miui.ui.version.name", null) != null
                        || prop.getProperty("ro.miui.internal.storage", null) != null;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }


    /**
     * 跳转到魅族的权限管理系统
     */
    private static boolean gotoMeizuPermission(Context context) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", context.getPackageName());
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 华为的权限管理页面
     */
    private static boolean gotoHuaweiPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    public static Intent getAppDetailSettingIntent(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return intent;
    }

    public static final int REQUEST_NECESSARY_PERMISSION = 101;//必要权限申请request code
    public static final int REQUEST_OPTIONAL_PERMISSION = 102;//可选权限申请request code

    public static boolean checkPermission(Activity context, List<String> permissionList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissionList) {
                int permissionCode = ActivityCompat.checkSelfPermission(context, permission);
//                int permissionCode = context.checkSelfPermission(permission);
                if (permissionCode != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void requestPermission(Activity context, int requestCode, List<String> permissionList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> noPermissionList = new ArrayList<>();
            for (String permission : permissionList) {
                int permissionCode = context.checkSelfPermission(permission);
                if (permissionCode != PackageManager.PERMISSION_GRANTED) {
                    noPermissionList.add(permission);
                }
            }
            if (noPermissionList.size() > 0) {
                String[] permissionString = new String[]{};
                permissionString = noPermissionList.toArray(permissionString);
                ActivityCompat.requestPermissions(context, permissionString, requestCode);
//                context.requestPermissions(permissionString, requestCode);
            }
        }
    }
}
