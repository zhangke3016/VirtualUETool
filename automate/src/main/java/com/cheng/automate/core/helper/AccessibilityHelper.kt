package com.cheng.automate.core.helper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.TargetApi
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.ViewCompat
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import com.cheng.automate.core.AutoAccessibilityService
import com.cheng.automate.core.config.ConfigCt
import com.cheng.automate.core.model.ElementBean
import com.lody.virtual.client.core.VirtualCore


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
     * 设置无障碍焦点
     * 若关闭，用户控件将失去焦点，不会播报内容
     * @param view  指定控件
     * @param focused true打开，false关闭
     */
    fun setAccessibilityFocusable(view: View?, focused: Boolean) {
        if (focused) {
            ViewCompat.setImportantForAccessibility(view, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
        } else {
            ViewCompat.setImportantForAccessibility(view, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO)
        }
    }

    /**
     * 根据元素获取nodeinfo
     */
    fun findNodeInfoByElement(element: ElementBean): Parcelable? {
        val rootNode = AutoAccessibilityService.service?.rootInActiveWindow ?: return null
        val appTaskInfo = VirtualCore.get().getForegroundTask(ConfigCt.AppName) ?: return null
        val currentWindow = appTaskInfo.topActivity.className ?: return null
        Log.e("FlowClick", "currentWindow:> $currentWindow, ${element.currentPage}")
        if (!element.resName.isNullOrEmpty()) {
            val resId = "${ConfigCt.AppName}:id/${element.resName}"
            Log.e("FlowClick", "find nodeRes $resId")
            return findNodeInfosById(rootNode, resId, element.rect)
        } else if (!element.text.isNullOrEmpty()) {
            Log.e("FlowClick", "find nodeText ${element.text}")
            return findNodeInfosByText(rootNode, element.text, element.rect)
        } else if (element.rect != null && !element.className.isNullOrEmpty()) {
            Log.e("FlowClick", "find OnlyClick NodeInfo ${element.text}")
            return OnlyClickNodeInfo()
        }
        Log.e("FlowClick", "find null NodeInfo!")
        return null
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
                    if (rect.contains(screenRect)) {
                        return item
                    }
                }
            } else {
                return list[0]
            }
        }
        return null
    }

    /**
     * 通过文本查找
     */
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

    /**
     * 通过ID查找
     */
    fun findNodeInfosById(nodeInfo: AccessibilityNodeInfo, resId: String?, rect: Rect?): AccessibilityNodeInfo? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val list = nodeInfo.findAccessibilityNodeInfosByViewId(resId)
            if (list != null && list.isNotEmpty()) {
                if (rect != null && list.size > 1) {
                    val screenRect = Rect()
                    for (item in list) {
                        item.getBoundsInScreen(screenRect)
                        if (rect.contains(screenRect)) {
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

    /** 通过控件查找 */
    fun findNodeInfosByClassName(rootNode: AccessibilityNodeInfo?, className: String?, rect: Rect): AccessibilityNodeInfo? {
        val classNames = mutableListOf<AccessibilityNodeInfo?>()
        recycleClassName(classNames, rootNode, className)
        if (classNames.isNullOrEmpty()) return null
        val screenRect = Rect()
        for (item in classNames) {
            item?.let {
                it.getBoundsInScreen(screenRect)
                if (rect.contains(screenRect)) {
                    return item
                }
            }
        }
        return null
    }

    private fun recycleClassName(classNames: MutableList<AccessibilityNodeInfo?>, info: AccessibilityNodeInfo?, className: String?, findChild: Boolean = true) {
        if (info == null || className.isNullOrEmpty()) return
        if (className == info.className) {
            classNames.add(info)
        }
        if (findChild && info.childCount > 0) {
            for (i in 0 until info.childCount) {
                if (info.getChild(i) != null) {
                    recycleClassName(classNames, info.getChild(i), className, false)
                }
            }
        }
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

    fun nodeInput(edtNode: AccessibilityNodeInfo, txt: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //android 5.0
            val arguments = Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, txt)
            edtNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            return true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) { //android 4.3
            //Application.getApplicationContext();
            val clipboard = VirtualCore.get().context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
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

    fun performClickByRect(rect: Rect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dispatch_gesture(
                    floatArrayOf(rect.centerX().toFloat(), rect.centerY().toFloat()),
                    floatArrayOf(rect.centerX().toFloat(), rect.centerY().toFloat()),
                    100,
                    50,
                    null)
        } else {
            GestureShellCmd.processClick(Point(rect.centerX(), rect.centerY()))
        }
    }

    /**
     * 手势模拟
     *
     * @param start_position 开始位置，长度为2的数组，下标0为x轴，下标1为y轴
     * @param end_position
     * @param startTime      开始间隔时间
     * @param duration       持续时间
     * @param callback       回调
     */
    @TargetApi(Build.VERSION_CODES.N)
    fun dispatch_gesture(
            start_position: FloatArray,
            end_position: FloatArray,
            startTime: Long,
            duration: Long,
            callback: GestureCallBack?
    ) {
        val path = Path()
        path.moveTo(start_position[0], start_position[1])
        path.lineTo(end_position[0], end_position[1])
        val builder = GestureDescription.Builder()
        val strokeDescription = GestureDescription.StrokeDescription(path, startTime, duration)
        val gestureDescription = builder.addStroke(strokeDescription).build()
        AutoAccessibilityService.service?.dispatchGesture(gestureDescription, object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription) {
                callback?.succ(gestureDescription)
            }

            override fun onCancelled(gestureDescription: GestureDescription) {
                callback?.fail(gestureDescription)
            }
        }, null)
    }
}