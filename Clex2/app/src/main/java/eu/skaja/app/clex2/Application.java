package eu.skaja.app.clex2;

import ly.img.android.PESDK;

// This activity is necessary for implementing the SDK that we use
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // The android_license can be found in the assets directory
        PESDK.init(this, "android_license");
    }
}