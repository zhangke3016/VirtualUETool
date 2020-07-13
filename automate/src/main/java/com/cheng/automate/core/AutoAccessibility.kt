package com.cheng.automate.core

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText
import com.cheng.automate.extend.*
import me.ele.uetool.base.MMKVUtil

/**
 * @author zijian.cheng
 * @date 2020/7/1
 */
class AutoAccessibility : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        baseContext.toast("服务开启")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val eventType = event!!.eventType
        val packageName = event.packageName
        Log.e("onAccessibilityEvent", "$eventType, $packageName, ${event.className}")
//        if (!MMKVUtil.mmkv.decodeBool("isStart", false)) {
//            return
//        }
        if (event.findPackageName("com.banban.kuxiu")) {
            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                //表示用户界面中可视的不同部分发生变化的事件。 32
                if (event.isWindowShowing("com.banban.kuxiu.liteav.optimal.LivePlayerActivity")) {
                    rootInActiveWindow.clickViewById("com.banban.kuxiu:id/iv_live_room_input")
                }
            } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                //表示窗口内容发生改变的事件  2048

            } else if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                //表示修改{@link android.widget.EditText}的文本的事件。16
            } else if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                //表示在{@link android.widget.EditText}中改变选择的事件。8192
                val editTexts: List<AccessibilityNodeInfo>? =
                    rootInActiveWindow.findViewsById<EditText>("com.banban.kuxiu:id/et_input_message")
                if (!editTexts.isNullOrEmpty()) {
                    Log.e("onAccessibilityEvent", "修改 EditText 的文本成功")
                    //rootInActiveWindow.clickViewById("com.banban.kuxiu:id/btnSendMsg")
                }
            } else if (eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
                //表示设置输入焦点的事件。8
                val editTexts: List<AccessibilityNodeInfo>? =
                    rootInActiveWindow.findViewsById<EditText>("com.banban.kuxiu:id/et_input_message")
                if (!editTexts.isNullOrEmpty()) {
//                    val text = MMKVHelper.mmkv.decodeString("inputText")
//                    for (nodeInfo in editTexts) {
//                        text?.let {
//                            nodeInfo.setContentText(it)
//                        }
//                    }
                }
            }
        } else if (packageName == "") {

        }
    }

    override fun onInterrupt() {
        baseContext.toast("onInterrupt")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        baseContext.toast("服务中断")
        return super.onUnbind(intent)
    }
}