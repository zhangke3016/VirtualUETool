package com.cheng.automate.core.helper

import android.graphics.Point
import android.util.Log
import com.cheng.automate.core.config.ConfigCt

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
}