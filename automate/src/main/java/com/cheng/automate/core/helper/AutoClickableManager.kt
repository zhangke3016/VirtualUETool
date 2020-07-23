package com.cheng.automate.core.helper

import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.cheng.automate.core.AutoAccessibilityService
import com.cheng.automate.core.model.ElementBean
import com.cheng.automate.core.model.MMKVUtil

/**
 * @author zijian.cheng
 * @date 2020/7/22
 */
object AutoClickableManager {

    const val mStepMsgDelayMillis: Long = 2000 //每一步延迟发送时间
    const val millisInFuture: Long = 10000 //寻找节点总时长
    const val countDownInterval: Long = 500 //寻找节点间隔时间
    const val NEXT_STEP = 0x00

    private var mDownTimer: DownTimer? = null
    private val mClickStepHandler: ClickStepHandler = ClickStepHandler()
    private lateinit var elementList: MutableList<ElementBean?>
    private var index = 0
    private const val loop = true

    fun findNodeInfo(callBack: CallBack, vararg orders: ElementBean?) {
        mDownTimer?.cancel()
        mDownTimer = DownTimer(callBack, *orders)
        mDownTimer?.start()
    }

    private fun stepFindNodeInfo(vararg orders: ElementBean?) {
        if (!orders.isNullOrEmpty()) {
            findNodeInfo(object : CallBack {
                override fun onSuccess(element: ElementBean, nodeInfo: AccessibilityNodeInfo?) {
                    if (nodeInfo == null) {
                        //only click
                        AccessibilityHelper.performClickByRect(element.rect)
                    } else {
                        AccessibilityHelper.performClick(nodeInfo)
                        if (!element.text.isNullOrEmpty() && nodeInfo.text != element.text) {
                            AccessibilityHelper.nodeInput(nodeInfo, element.text)
                        }
                    }
                    mClickStepHandler.sendEmptyMessageDelayed(NEXT_STEP, mStepMsgDelayMillis)
                }

                override fun onFinish() {
                    Log.e(" ", "not find!")
                }
            }, *orders)
        }
    }

    private fun getCurrentElement(position: Int): ElementBean? {
        return when {
            position < elementList.size -> {
                elementList[position]
            }
            loop -> {
                index = 0
                elementList[0]
            }
            else -> {
                null
            }
        }
    }

    fun start() {
        index = 0
        elementList = MMKVUtil.getInstance().getElements("elementBeans")
        mClickStepHandler.removeCallbacksAndMessages(null)
        mClickStepHandler.sendEmptyMessage(NEXT_STEP)
    }

    fun stop() {
        mClickStepHandler.removeCallbacksAndMessages(null)
        mDownTimer?.callBack = null
        mDownTimer?.cancel()
    }

    class ClickStepHandler : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == NEXT_STEP) {
                stepFindNodeInfo(getCurrentElement(index++))
            }
        }
    }

    class DownTimer(var callBack: CallBack?, private vararg val orders: ElementBean?)
        : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {
            if (AutoAccessibilityService.service?.isStart() == false) {
                cancel()
                return
            }
            for (item in orders) {
                if (item != null) {
                    val nodeInfo = AccessibilityHelper.findNodeInfoByElement(item)
                    if (nodeInfo is OnlyClickNodeInfo) {
                        callBack?.onSuccess(item)
                        cancel()
                        return
                    } else if (nodeInfo is AccessibilityNodeInfo) {
                        callBack?.onSuccess(item, nodeInfo)
                        cancel()
                        return
                    }
                }
            }
        }

        override fun onFinish() {
            callBack?.onFinish()
        }
    }

    interface CallBack {
        fun onSuccess(element: ElementBean, nodeInfo: AccessibilityNodeInfo? = null)

        fun onFinish()
    }
}