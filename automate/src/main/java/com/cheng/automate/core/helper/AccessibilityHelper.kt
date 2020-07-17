package com.cheng.automate.core.helper

import android.os.Build
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

    /** 通过文本查找 */
    fun findNodeInfosByText(nodeInfo: AccessibilityNodeInfo, text: String?, i: Int): AccessibilityNodeInfo? {
        val list = nodeInfo.findAccessibilityNodeInfosByText(text)
        if (list == null || list.isEmpty()) {
            return null
        }
        return if (i == -1) list[list.size - 1] else list[i]
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

    fun findNodeInfosById(nodeInfo: AccessibilityNodeInfo, resId: String?, i: Int): AccessibilityNodeInfo? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val list = nodeInfo.findAccessibilityNodeInfosByViewId(resId)
            if (list != null && list.isNotEmpty()) {
                return if (i == -1) list[list.size - 1] else list[i]
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
}