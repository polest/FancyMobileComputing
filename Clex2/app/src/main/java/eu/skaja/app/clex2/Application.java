package eu.skaja.app.clex2;

import ly.img.android.PESDK;

/**
 * Created by Skaja on 15.06.2017.
 */

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PESDK.init(this, "android_license");
    }
}