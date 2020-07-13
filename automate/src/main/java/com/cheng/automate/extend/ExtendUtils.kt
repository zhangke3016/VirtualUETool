package com.cheng.automate.extend

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import kotlin.reflect.KClass


/**
 * @author zijian.cheng
 * @date 2020/7/1
 */
fun Context?.toast(value: CharSequence?) {
    Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
}

/**
 * 检查当前包名
 */
fun AccessibilityEvent.findPackageName(packageName: String?): Boolean {
    return this.packageName == packageName
}

/**
 * 判断activityName是否在显示
 */
fun AccessibilityEvent.isWindowShowing(activityName: String): Boolean {
    return activityName == this.className
}

fun <T> checkNodeInfoType(item: Any, view: T): Boolean {
    return try {
        if (item is AccessibilityNodeInfo) {
            item.className.toString() == (view as Class<*>).name
        } else {
            (item as KClass<*>).qualifiedName == (view as Class<*>).name
        }
    } catch (e: Exception) {
        Log.e("onAccessibilityEvent", "error:>>${view.toString()}, ${e.localizedMessage}")
        false
    }
}

/**
 * 根据text找到 view
 */
inline fun <reified T> AccessibilityNodeInfo.findViewsByText(text: String): List<AccessibilityNodeInfo>? {
    val nodeInfoList = this.findAccessibilityNodeInfosByText(text)
    if (!nodeInfoList.isNullOrEmpty()) {
        val type = T::class.java
        if (!checkNodeInfoType(View::class, type))
            nodeInfoList.filter {
                checkNodeInfoType(it, type)
            }
        return nodeInfoList
    }
    return null
}

/**
 * 根据 id 找到 view
 */
inline fun <reified T> AccessibilityNodeInfo.findViewsById(id: String): List<AccessibilityNodeInfo>? {
    val nodeInfoList = this.findAccessibilityNodeInfosByViewId(id)
    if (!nodeInfoList.isNullOrEmpty()) {
        val type = T::class.java
        if (!checkNodeInfoType(View::class, type))
            nodeInfoList.filter {
                checkNodeInfoType(it, type)
            }
        return nodeInfoList
    }
    return null
}

/**
 * 设置view值
 */
fun AccessibilityNodeInfo.setContentText(context: String?) {
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
            //android>21 = 5.0时 用ACTION_SET_TEXT
            val arguments = Bundle()
            arguments.putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                context
            )
            //焦点
            this.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            this.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
            //焦点
            this.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            //粘贴剪切板内容
            this.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
        else -> {

        }
    }
}

/**
 * 点击view
 */
fun AccessibilityNodeInfo.clickViewById(id: String) {
    val items = findViewsById<View>(id)
    if (!items.isNullOrEmpty()) {
        for (item in items) {
            item.performClick()
        }
    }
}

/**
 * 模拟点击事件
 */
fun AccessibilityNodeInfo.performClick(clickParent: Boolean = true) {
    if (isClickable) {
        performAction(AccessibilityNodeInfo.ACTION_CLICK)
    } else if (this.parent != null && clickParent) {
        this.parent.performClick()
    }
}