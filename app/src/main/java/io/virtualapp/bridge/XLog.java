package io.virtualapp.bridge;

import android.util.Log;

/**
 * Created by bmax on 2018/4/20.
 */

public class XLog {

    public static final String TAG = "XLog";

    public static int v(String s){
        return Log.v(TAG,s);
    }
    public static int i(String s){
        return Log.i(TAG,s);
    }
    public static int d(String s){
        return Log.d(TAG,s);
    }
    public static int w(String s){
        return Log.w(TAG,s);
    }
    public static int e(String s){
        return Log.e(TAG,s);
    }
    public static int e(String s,Throwable t){
        return Log.e(TAG,s,t);
    }
}
