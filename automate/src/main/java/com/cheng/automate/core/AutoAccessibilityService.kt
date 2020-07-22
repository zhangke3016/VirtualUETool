package com.cheng.automate.core

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
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
        var service: AutoAccessibilityService? = null
        private val job: ManageAccessibilityJob = ManageAccessibilityJob.instance
        private val hookPackName = ConfigCt.AppName
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

    fun isStart() = MMKVUtil.getInstance().decodeBool("isStart", false)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val eventType = event!!.eventType
        val packageName = event.packageName
        Log.e("onAccessibilityEvent", "$eventType, $packageName, ${event.className}")
        val userId = VirtualCore.get().myUserId()
        if (!isStart()
                || !VirtualCore.get().isEngineLaunched
                || !VirtualCore.get().isAppRunning(hookPackName, userId)
                || event.packageName != "io.virtualapp268") {
            return
        }

        if (VirtualCore.get().getForegroundTask(hookPackName) != null) {
            //应用在前台显示
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