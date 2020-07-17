package com.cheng.automate.core.jobs

import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.cheng.automate.core.AutoAccessibilityService
import com.cheng.automate.core.base.BaseAccessibilityJob
import com.cheng.automate.core.config.ConfigCt
import com.cheng.automate.core.helper.AccessibilityHelper
import com.cheng.automate.core.helper.FlowClickDataHelper
import com.cheng.automate.core.model.ElementBean
import com.cheng.automate.core.model.MMKVUtil
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
        val currentElement = FlowClickDataHelper.getCurrentElement()
        currentElement?.let {
            val rootNode = event.source ?: return
            val appTaskInfo = VirtualCore.get().getForegroundTask(ConfigCt.AppName)
            if (appTaskInfo != null) {
                val currentWindow = appTaskInfo.topActivity.className ?: return
                Log.e("FlowClick", "currentWindow:>" + currentWindow)
                if (currentElement.currentActivity == currentWindow) {
                    val resId = "${ConfigCt.AppName}:id/${currentElement.resName}"
                    val nodeInfo = AccessibilityHelper.findNodeInfosById(rootNode, resId, 0)
                    if (nodeInfo != null) {
                        FlowClickDataHelper.ascendIndex()
                        AccessibilityHelper.performClick(nodeInfo)
                        Log.e("FlowClick", "find node ${nodeInfo.viewIdResourceName}, $resId")
                    }
                }
            }
        }
    }

    override fun onWorking() {


    }
}