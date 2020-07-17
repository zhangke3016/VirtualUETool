package com.cheng.automate.core.jobs

import android.view.accessibility.AccessibilityEvent
import com.cheng.automate.core.base.BaseAccessibilityJob

/**
 * @author zijian.cheng
 * @date 2020/7/16
 */
class ExeClick(private val mType: Int) : BaseAccessibilityJob(null) {

    companion object {
        @Volatile
        private var instance: ExeClick? = null
        fun getInstance(clickType: Int) =
                instance ?: synchronized(this) {
                    instance ?: ExeClick(clickType).also { instance = it }
                }
    }

    override fun onWorking() {

    }

    override fun onReceiveJob(event: AccessibilityEvent?) {
        super.onReceiveJob(event)
        if (!mIsEventWorking) return
        if (!mIsTargetPackageName) return
    }
}