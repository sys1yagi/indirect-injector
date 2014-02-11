package com.sys1yagi.indirectinjector;

import android.util.Log;

public class Logger {

    private final static String TAG = IndirectInjector.class.getSimpleName();

    static void d(String message) {
        Log.d(TAG, message);
    }
}
