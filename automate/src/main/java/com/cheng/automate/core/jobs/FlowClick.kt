package com.cheng.automate.core.jobs

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.cheng.automate.core.AutoAccessibilityService
import com.cheng.automate.core.base.BaseAccessibilityJob
import com.cheng.automate.core.config.ConfigCt
import com.cheng.automate.core.helper.AccessibilityHelper
import com.lody.virtual.client.core.VirtualCore

/**
 * @author zijian.cheng
 * @date 2020/7/16
 */
class FlowClick : BaseAccessibilityJob(null) {

    companion object {
        val instance: FlowClick by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FlowClick()
        }
    }

    override fun onCreateJob(service: AutoAccessibilityService?) {
        super.onCreateJob(service)
        onEventStart()
    }

    override fun onReceiveJob(event: AccessibilityEvent?) {
        super.onReceiveJob(event)
        if (!mIsEventWorking) return
        if (!mIsTargetPackageName) return
        if (event?.packageName == null) return
//        val currentElement = ElementDataHelper.getCurrentElement()
//        currentElement?.let {
//            val rootNode = event.source ?: return
//            val appTaskInfo = VirtualCore.get().getForegroundTask(ConfigCt.AppName) ?: return
//            //AccessibilityHelper.setAccessibilityFocusable(, true)
//            val currentWindow = appTaskInfo.topActivity.className ?: return
//            Log.e("FlowClick", "currentWindow:> $currentWindow")
//            if (currentElement.currentPage == currentWindow) {
//                val nodeInfo = if (currentElement.resName.isNullOrEmpty()) {
//                    if (currentElement.text.isNullOrEmpty()) {
//                        if (it.rect != null && !currentElement.className.isNullOrEmpty()) {
//                            AccessibilityHelper.performClickByRect(it.rect)
//                            return
//                        } else {
//                            return
//                        }
//                    } else {
//                        Log.e("FlowClick", "find nodeText ${currentElement.text}")
//                        AccessibilityHelper.findNodeInfosByText(rootNode, currentElement.text, it.rect)
//                    }
//                } else {
//                    val resId = "${ConfigCt.AppName}:id/${currentElement.resName}"
//                    Log.e("FlowClick", "find nodeRes $resId")
//                    AccessibilityHelper.findNodeInfosById(rootNode, resId, it.rect)
//                } ?: return
//
//                AccessibilityHelper.performClick(nodeInfo)
//                if (!it.text.isNullOrEmpty() && nodeInfo.text != it.text) {
//                    AccessibilityHelper.nodeInput(nodeInfo, it.text)
//                }
//                ElementDataHelper.ascendIndex()
//            }
//        }
    }

    override fun onWorking() {


    }
}