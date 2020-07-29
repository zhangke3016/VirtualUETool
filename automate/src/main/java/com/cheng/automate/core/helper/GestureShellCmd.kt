package com.cheng.automate.core.helper

import android.graphics.Point
import android.graphics.PointF
import android.util.Log
import me.ele.uetool.base.config.ConfigCt

/**
 * @author zijian.cheng
 * @date 2020/7/21
 */
object GestureShellCmd {

    /**
     * 点击
     *
     * @param pos
     * 键值
     */
    fun processClick(pos: Point) {
        val sOrder = "input tap " + pos.x.toString() + " " + pos.y.toString()
        Log.i(ConfigCt.TAG, sOrder)
        ShellUtils.execCommand(sOrder, false, false)
    }

    /**
     * 滑动
     */
    fun processSwipe(p1: PointF, p2: PointF, duration: Long) {
        //adb shell input swipe 100 200 500 600 900 从(100,200)滑动到(500,600)总花费900ms
        val sOrder = "input swipe " + p1.x.toInt().toString() + " " + p1.y.toInt().toString() + " " + p2.x.toInt().toString() + " " + p2.y.toInt().toString() + " " + duration
        Log.i(ConfigCt.TAG, sOrder)
        ShellUtils.execCommand(sOrder, false, false)
    }
}