package com.cheng.automate.core

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import com.cheng.automate.core.config.ConfigCt
import com.cheng.automate.core.jobs.ManageAccessibilityJob
import com.cheng.automate.core.model.MMKVUtil
import com.cheng.automate.extend.toast
import com.lody.virtual.client.core.VirtualCore

/**
 * @author zijian.cheng
 * @date 2020/7/1
 */
class AutoAccessibilityService : AccessibilityService() {

    companion object {
        private var service: AutoAccessibilityService? = null
        private val job: ManageAccessibilityJob = ManageAccessibilityJob.instance
        private val hookPackName = ConfigCt.AppName

        fun isRunning(): Boolean {
            service?.let {
                val accessibilityManager = it.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
                val info: AccessibilityServiceInfo = it.serviceInfo ?: return false
                val list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
                val iterator: Iterator<AccessibilityServiceInfo> = list.iterator()
                var isConnect = false
                while (iterator.hasNext()) {
                    val i = iterator.next()
                    if (i.id == info.id) {
                        isConnect = true
                        break
                    }
                }
                return isConnect
            }
            return false
        }
    }

    override fun onCreate() {
        super.onCreate()
        service = this
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        service = this
        job.onCreateJob(service)

        baseContext.toast("服务开启")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val begin = System.currentTimeMillis()
        val eventType = event!!.eventType
        val packageName = event.packageName
        Log.e("onAccessibilityEvent", "$eventType, $packageName, ${event.className}")
        if (!MMKVUtil.getInstance().decodeBool("isStart", false)) {
            return
        }
        val userId = VirtualCore.get().myUserId()
        if (!VirtualCore.get().isEngineLaunched
                || !VirtualCore.get().isAppRunning(hookPackName, userId)) {
            return
        }

        val foregroundTask = VirtualCore.get().getForegroundTask(hookPackName)
        if (foregroundTask != null) {
            //应用在前台显示
            //val currentWindow = foregroundTask.topActivity.className
            //event.packageName = currentWindow
            //Log.e("onAccessibilityEvent>>", "$currentWindow, ${System.currentTimeMillis() - begin}")
            job.onReceiveJob(event)
        }
//        VirtualCore.get().isPackageLaunchable("com.memezhibo.android")
//        VirtualCore.get().isForeground("com.memezhibo.android")
//        VirtualCore.get().isAppInstalled("com.memezhibo.android")
//        VirtualCore.get().getLaunchIntent("com.memezhibo.android", 0)
//        VirtualCore.get().getResources("com.memezhibo.android")
//        VirtualCore.get().isPackageLaunched(0, "com.memezhibo.android")
//        VirtualCore.get().isOutsideInstalled("com.memezhibo.android")
//        VirtualCore.get().resolveActivityInfo(VirtualCore.get().getLaunchIntent("com.memezhibo.android", 0), 0)
//        "io.virtualapp268"

//        if (event.findPackageName("com.banban.kuxiu")) {
//            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//                //表示用户界面中可视的不同部分发生变化的事件。 32
//                if (event.isWindowShowing("com.banban.kuxiu.liteav.optimal.LivePlayerActivity")) {
//                    rootInActiveWindow.clickViewById("com.banban.kuxiu:id/iv_live_room_input")
//                }
//            } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
//                //表示窗口内容发生改变的事件  2048
//
//            } else if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
//                //表示修改{@link android.widget.EditText}的文本的事件。16
//            } else if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
//                //表示在{@link android.widget.EditText}中改变选择的事件。8192
//                val editTexts: List<AccessibilityNodeInfo>? =
//                    rootInActiveWindow.findViewsById<EditText>("com.banban.kuxiu:id/et_input_message")
//                if (!editTexts.isNullOrEmpty()) {
//                    Log.e("onAccessibilityEvent", "修改 EditText 的文本成功")
//                    //rootInActiveWindow.clickViewById("com.banban.kuxiu:id/btnSendMsg")
//                }
//            } else if (eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
//                //表示设置输入焦点的事件。8
//                val editTexts: List<AccessibilityNodeInfo>? =
//                    rootInActiveWindow.findViewsById<EditText>("com.banban.kuxiu:id/et_input_message")
//                if (!editTexts.isNullOrEmpty()) {
////                    val text = MMKVHelper.mmkv.decodeString("inputText")
////                    for (nodeInfo in editTexts) {
////                        text?.let {
////                            nodeInfo.setContentText(it)
////                        }
////                    }
//                }
//            }
//        } else if (packageName == "") {
//
//        }
    }

    override fun onInterrupt() {
        baseContext.toast("onInterrupt")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        baseContext.toast("服务中断")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.onStopJob()
        service = null
    }
}