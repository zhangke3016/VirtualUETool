package com.cheng.automate.core.jobs

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.cheng.automate.core.AutoAccessibilityService
import com.cheng.automate.core.base.BaseAccessibilityJob
import com.cheng.automate.core.config.ConfigCt
import com.cheng.automate.core.helper.AccessibilityHelper

/**
 * @author zijian.cheng
 * @date 2020/7/16
 */
class ProtectMe : BaseAccessibilityJob(null) {

    companion object {
        val instance: ProtectMe by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ProtectMe()
        }
    }

    override fun onCreateJob(service: AutoAccessibilityService?) {
        super.onCreateJob(service)
        onEventStart()
    }

    override fun onWorking() {

    }

    override fun onReceiveJob(event: AccessibilityEvent?) {
        super.onReceiveJob(event)
        if (!mIsEventWorking) return
        if (!mIsTargetPackageName) return

        val rootNode = event!!.source ?: return
        clickCancelUninstall(rootNode)
        clickCancelStopAccessibility(rootNode)
        clickPermitAccessibility(rootNode)
    }

    /**
     * 点击取消卸载按钮；
     */
    private fun clickCancelUninstall(rootNode: AccessibilityNodeInfo?): Boolean {
        rootNode?.let {
            var nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, ConfigCt.AppName, 0)
            if (nodeInfo == null) return false
            nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "卸载", 0)
            if (nodeInfo == null) {
                nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "删除", 0)
                if (nodeInfo == null) return false
            }
            nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "取消", AccessibilityHelper.WIDGET_BUTTON)
            if (nodeInfo == null) return false
            AccessibilityHelper.performClick(nodeInfo)
            return true
        }
        return false
    }

    /**
     * 点击取消停用按钮；
     */
    private fun clickCancelStopAccessibility(rootNode: AccessibilityNodeInfo) {
        if (rootNode.packageName == null) return
        if (rootNode.packageName.toString() != AccessibilityHelper.PACKAGE_NAME_SETTING) return
        var nodeInfo: AccessibilityNodeInfo? = AccessibilityHelper.findNodeInfosByText(rootNode, ConfigCt.AppName, 0)
                ?: return
        nodeInfo = AccessibilityHelper.findNodeInfosByText(rootNode, "停用", 0)
        if (nodeInfo == null) return
        nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "取消", AccessibilityHelper.WIDGET_BUTTON)
        if (nodeInfo == null) return
        AccessibilityHelper.performClick(nodeInfo)
        return
    }

    /**
     * 点击允许按钮；
     */
    private fun clickPermitAccessibility(rootNode: AccessibilityNodeInfo?) {
        var nodeInfo: AccessibilityNodeInfo? = AccessibilityHelper.findNodeInfosByText(rootNode!!, ConfigCt.AppName, 0)
                ?: return
        nodeInfo = AccessibilityHelper.findNodeInfosByTextAndClassName(rootNode, "允许", AccessibilityHelper.WIDGET_BUTTON)
        if (nodeInfo == null) return
        AccessibilityHelper.performClick(nodeInfo)
        return
    }
}