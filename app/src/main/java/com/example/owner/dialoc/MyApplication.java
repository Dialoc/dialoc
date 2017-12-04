package com.example.owner.dialoc;

import android.app.Application;

/**
 * Created by thomas on 12/4/17.
 */


public class MyApplication extends Application {
    protected static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}