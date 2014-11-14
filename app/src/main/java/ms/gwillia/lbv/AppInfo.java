package ms.gwillia.lbv;

import android.graphics.drawable.Drawable;

/**
 * Created by encima on 05/11/14.
 */
public class AppInfo {

    private String appName;
    private String packageName;
    private Drawable icon;

    public AppInfo() {

    }

    public AppInfo(String name, String pkg, Drawable icon) {
        this.appName = name;
        this.packageName = pkg;
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return this.appName + ": " + this.packageName;
    }
}
