package com.minew.trackerfinderdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * @author boyce
 * @date 2018/5/16 15:47
 */
public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private        Activity      mActivity;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        this.registerActivityLifecycleCallbacks(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public Activity getActivity() {
        return mActivity;
    }
}
