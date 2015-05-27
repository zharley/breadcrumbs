package com.node22.breadcrumbs;

import android.util.Log;

/**
 * Created by zharley on 15-05-27.
 */
public class Util {
    public static final String LOG_TAG = "com.node22.breadcrumbs";

    public static void debug(String message) {
        Log.d(Util.LOG_TAG, message);
    }

    public static void error(String message) {
        Log.e(Util.LOG_TAG, message);
    }
}
