package com.cheng.automate.core.jobs

import android.app.Notification
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.cheng.automate.core.AutoAccessibilityService
import com.cheng.automate.core.base.BaseAccessibilityJob
import me.ele.uetool.base.config.ConfigCt

/**
 * @author zijian.cheng
 * @date 2020/7/16
 */
class AccessibilitySaveNotification : BaseAccessibilityJob(null) {

    var mFilename: String? = null

    companion object {
        val instance: AccessibilitySaveNotification by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AccessibilitySaveNotification()
        }
    }

    override fun onCreateJob(service: AutoAccessibilityService?) {
        super.onCreateJob(service)
        onEventStart()
        mFilename = getNotificationFileName()
    }

    override fun onWorking() {

    }

    override fun onReceiveJob(event: AccessibilityEvent?) {
        super.onReceiveJob(event)
        if (!mIsEventWorking) return
        if (!mIsTargetPackageName) return
        if (event!!.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            val data = event.parcelableData //获取Parcelable对象
            if (data is Notification) { //判断是否是Notification对象
                val pkg = event.packageName.toString()
                val sTime: String = "";//Funcs.milliseconds2String(System.currentTimeMillis())
                val text = data.tickerText ?: return
                val info = "$sTime($pkg)\r\n$text\r\n"
                if (ConfigCt.DEBUG) Log.i(TAG, "${text}, ${info}")
                //Funcs.saveInfo2File(info, mFilename, true)
                //AnalyzeView(notification.contentView,pkg);
            }
        }
    }

    /**
     * 获取通知文件名
     */
    private fun getNotificationFileName(): String? {
        return ConfigCt.LocalPath + ConfigCt.appID + "sbn.log"
    }
}