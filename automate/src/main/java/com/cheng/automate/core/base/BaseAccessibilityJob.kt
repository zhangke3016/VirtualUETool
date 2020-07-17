package com.cheng.automate.core.base

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.cheng.automate.core.AutoAccessibilityService
import com.cheng.automate.core.config.ConfigCt

/**
 * @author zijian.cheng
 * @date 2020/7/16
 */
abstract class BaseAccessibilityJob(pkgs: Array<String?>?) : AccessibilityJob {

    val TIME_WORKING_INTERVAL = 200 //事件驱动的刷新频率；
    val TIME_WORKING_CONTINUE = 1000 * 60 * 1 //事件驱动的持续时间；

    var TAG: String? = null
    var TAG2: String? = null
    var mPkgs: Array<String?>? = null  //所处理的包;

    var service: AutoAccessibilityService? = null
    var context: Context? = null

    var mIsTimeWorking = false //是否开始刷新处理；
    var mIsEventWorking = false //是否开始事件处理；
    var mIsTargetPackageName = false //是否是本程序处理包；
    var eventType = 0 //事件类型;
    var mCurrentUI = ""

    val mHandler = Handler(Looper.getMainLooper())
    val runnableEventStop = Runnable { mIsEventWorking = false }
    val runnableTimeWorkStop = Runnable { mIsTimeWorking = false }
    val runnableTimeWork: Runnable = object : Runnable {
        override fun run() {
            if (!mIsTimeWorking) return
            onWorking()
            mHandler.postDelayed(this, TIME_WORKING_INTERVAL.toLong())
        }
    }

    init {
        this.TAG = ConfigCt.TAG
        this.TAG2 = ConfigCt.TAG2
        this.mPkgs = pkgs
    }

    /**
     * (创建工作任务)
     */
    override fun onCreateJob(service: AutoAccessibilityService?) {
        this.service = service
        this.TAG = ConfigCt.TAG
        this.TAG2 = ConfigCt.TAG2
        this.context = service!!.applicationContext
    }

    override fun onStopJob() {
        this.service = null
        this.context = null
    }

    override fun isEnable(): Boolean {
        return service != null
    }

    /**
     * 是否是所处理的包
     */
    override fun isTargetPackageName(pkg: String?): Boolean {
        if (mPkgs.isNullOrEmpty()) return true
        if (pkg.isNullOrEmpty()) return false
        for (i in mPkgs!!.indices) {
            if (mPkgs!![i] == pkg) return true
        }
        return false
    }

    /**
     * 返回所处理的包
     */
    override fun getTargetPackageName(): Array<String?>? {
        return mPkgs
    }

    /**
     * 事件驱动流程
     */
    override fun onReceiveJob(event: AccessibilityEvent?) {
        if (!mIsEventWorking) return
        if (event?.packageName == null) return
        if (!isTargetPackageName(event.packageName.toString())) {
            mIsTargetPackageName = false
            return
        }
        mIsTargetPackageName = true
        eventType = event.eventType
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.className == null) return
            mCurrentUI = event.className.toString()
        }
    }

    override fun onEventStart() {
        mHandler.removeCallbacks(runnableEventStop)
        mIsEventWorking = true
    }

    override fun closeEventWorking() {
        mIsEventWorking = false
    }

    override fun onEventTimeStart() {
        mHandler.removeCallbacks(runnableEventStop)
        mHandler.postDelayed(runnableEventStop, TIME_WORKING_CONTINUE.toLong())
        mIsEventWorking = true
    }

    override fun onTimeStart() {
        mHandler.removeCallbacks(runnableTimeWork)
        mHandler.removeCallbacks(runnableTimeWorkStop)
        mIsTimeWorking = true
        mHandler.postDelayed(runnableTimeWorkStop, TIME_WORKING_CONTINUE.toLong())
        mHandler.postDelayed(runnableTimeWork, 10)
    }

    override fun closeTimeWorking() {
        mIsTimeWorking = false
    }
}