package com.cheng.automate.core.helper;

import android.accessibilityservice.GestureDescription;

/**
 * 2019/4/21
 * 13:20
 * Levine
 * wechat 1483232332
 */
public interface GestureCallBack {
    void succ(GestureDescription gestureDescription);

    void fail(GestureDescription gestureDescription);
}
