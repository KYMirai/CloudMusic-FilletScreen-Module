package xyz.kymirai.cloudmusic;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.pm.PackageManager;


class VersionControl {
    static String clazz = "com.netease.cloudmusic.activity.n";

    static {
        try {
            Application application = AndroidAppHelper.currentApplication();
            switch (application.getPackageManager().getPackageInfo(application.getPackageName(), PackageManager.GET_ACTIVITIES).versionName) {
                case "7.0.20":
                    clazz = "com.netease.cloudmusic.activity.o";
                    break;
                case "6.4.5":
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
