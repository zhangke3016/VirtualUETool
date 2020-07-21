package com.cheng.automate.core.helper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityNodeInfo

/**
 * @author zijian.cheng
 * @date 2020/7/16
 */
object AccessibilityHelper {
    const val WIDGET_TEXT = "android.widget.TextView"
    const val WIDGET_BUTTON = "android.widget.Button"
    const val WIDGET_CHECKBOX = "android.widget.CheckBox"
    const val WIDGET_EDIT = "android.widget.EditText"
    const val PACKAGE_NAME_SETTING = "com.android.settings"
    const val WINDOW_NOTIFICATION_ACCESS_UI = "com.android.settings.Settings\$NotificationAccessSettingsActivity"

    /** 得到rootNode */
    fun getRootNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        var parent = node.parent
        var tmp = node
        while (parent != null) {
            tmp = parent
            parent = parent.parent
        }
        return tmp
    }

    /**
     * 通过文本查找
     */
    fun findNodeInfosByText(nodeInfo: AccessibilityNodeInfo, text: String?, i: Int): AccessibilityNodeInfo? {
        val list = nodeInfo.findAccessibilityNodeInfosByText(text)
        if (list == null || list.isEmpty()) {
            return null
        }
        return if (i == -1) list[list.size - 1] else list[i]
    }

    /**
     * 通过文本查找
     */
    fun findNodeInfosByText(nodeInfo: AccessibilityNodeInfo, text: String?, rect: Rect?): AccessibilityNodeInfo? {
        val list = nodeInfo.findAccessibilityNodeInfosByText(text)
        if (list != null && list.isNotEmpty()) {
            if (rect != null && list.size > 1) {
                val screenRect = Rect()
                for (item in list) {
                    item.getBoundsInScreen(screenRect)
                    if (screenRect.contains(rect)) {
                        return item
                    }
                }
            } else {
                return list[0]
            }
        }
        return null
    }

    /** 通过文本查找 */
    fun findNodeInfosByTextAndClassName(rootNode: AccessibilityNodeInfo, text: String?, className: String): AccessibilityNodeInfo? {
        val list = rootNode.findAccessibilityNodeInfosByText(text)
        if (list == null || list.isEmpty()) {
            return null
        }
        for (node in list) {
            val cName = node.className.toString()
            if (cName == className) return node
        }
        return null
    }

    fun findNodeInfosById(nodeInfo: AccessibilityNodeInfo, resId: String?, rect: Rect?): AccessibilityNodeInfo? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val list = nodeInfo.findAccessibilityNodeInfosByViewId(resId)
            if (list != null && list.isNotEmpty()) {
                if (rect != null && list.size > 1) {
                    val screenRect = Rect()
                    for (item in list) {
                        item.getBoundsInScreen(screenRect)
                        if (screenRect.contains(rect)) {
                            return item
                        }
                    }
                } else {
                    return list[0]
                }
            }
        }
        return null
    }

    /** 点击事件 */
    fun performClick(nodeInfo: AccessibilityNodeInfo?): Boolean {
        if (nodeInfo == null) {
            return false
        }
        return if (nodeInfo.isClickable) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        } else {
            performClick(nodeInfo.parent)
        }
    }

    fun nodeInput(context: Context, edtNode: AccessibilityNodeInfo, txt: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //android 5.0
            val arguments = Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, txt)
            edtNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            return true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) { //android 4.3
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("text", txt)
            clipboard.primaryClip = clip
            //edtNode.fo
            edtNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            ////粘贴进入内容
            edtNode.performAction(AccessibilityNodeInfo.ACTION_PASTE)
            return true
        }
        return false
    }
}