package io.virtualapp.hook;

import android.util.Log;

public class HookTest {

    private static final String TAG = "HookTest";

    public static void hook(Object object, String str) {
        Log.w(TAG, "hook test str: " + str);
        str = str + "hook";
        backup(object, str);
    }

    public static void backup(Object object, String str) {
        Log.w(TAG, "hook setOnclick should not be here");
    }
}
