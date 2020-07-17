package com.cheng.automate.core.base

import android.view.accessibility.AccessibilityEvent
import com.cheng.automate.core.AutoAccessibilityService

/**
 * @author zijian.cheng
 * @date 2020/7/16
 */
interface AccessibilityJob {

    fun getTargetPackageName(): Array<String?>? //获取要处理的包；

    fun isTargetPackageName(pkg: String?): Boolean //是否是目标包；

    fun isEnable(): Boolean //是否可用；

    fun onCreateJob(service: AutoAccessibilityService?) //创建工作；

    fun onStopJob() //停止工作；

    fun onReceiveJob(event: AccessibilityEvent?) //事件驱动；

    fun onEventStart() //开始事件处理；

    fun onEventTimeStart() //开始定时事件处理；

    fun closeEventWorking() //关闭事件处理；

    fun onWorking() //刷新处理流程；

    fun onTimeStart() //开始刷新处理；

    fun closeTimeWorking() //关闭刷新处理；

}