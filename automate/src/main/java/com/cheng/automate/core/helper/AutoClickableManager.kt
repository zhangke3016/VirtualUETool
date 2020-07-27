package com.cheng.automate.core.helper

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
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

    const val mStepMsgDelayMillis: Long = 1000 //每一步延迟发送时间
    const val millisInFuture: Long = 10000 //寻找节点总时长
    const val countDownInterval: Long = 500 //寻找节点间隔时间
    const val NEXT_STEP = 0x00
    const val FIND_ELEMENT = 0x01

    private var mDownTimer: DownTimer = DownTimer()
    private val mClickStepHandler: ClickStepHandler = ClickStepHandler(Looper.getMainLooper())
    private lateinit var elementList: MutableList<ElementBean?>
    private const val loop = true

    private var currentOrders: Array<out ElementBean?>? = null
    private var index = 0

    class ClickStepHandler(mainLooper: Looper) : Handler(mainLooper) {
        override fun handleMessage(msg: Message) {
            if (msg.what == NEXT_STEP) {
                findNodeInfoByTimer(getQueueElement(index))
            } else if (msg.what == FIND_ELEMENT) {
                currentOrders?.let {
                    for (item in it) {
                        if (item != null) {
                            val nodeInfo = AccessibilityHelper.findNodeInfoByElement(item)
                            if (nodeInfo is OnlyClickNodeInfo) {
                                stop()
                                onSuccess(item)
                                return
                            } else if (nodeInfo is AccessibilityNodeInfo) {
                                stop()
                                onSuccess(item, nodeInfo)
                                return
                            }
                        }
                    }
                }
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
        currentOrders = null
        mDownTimer.cancel()
        mClickStepHandler.removeCallbacksAndMessages(null)
    }

    private fun findNodeInfoByTimer(vararg orders: ElementBean?) {
        mDownTimer.cancel()
        currentOrders = orders
        mDownTimer.start()
    }

    fun onSuccess(element: ElementBean, nodeInfo: AccessibilityNodeInfo? = null) {
        if (nodeInfo == null) {
            //only click
            index += 1
            AccessibilityHelper.performClickByRect(element.rect)
        } else {
            if (!element.text.isNullOrEmpty() && nodeInfo.text != element.text) {
                AccessibilityHelper.nodeInput(nodeInfo, element.text)
            }
            index += 1
            AccessibilityHelper.performClick(nodeInfo)
        }
        mClickStepHandler.removeCallbacksAndMessages(null)
        mClickStepHandler.sendEmptyMessageDelayed(NEXT_STEP, mStepMsgDelayMillis)
    }

    fun onTimerFinish() {
        Log.e("FlowClick", "not find!")
    }

    class DownTimer : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) {
            if (AutoAccessibilityService.service?.isStart() == false) {
                cancel()
                return
            }
            mClickStepHandler.removeCallbacksAndMessages(null)
            mClickStepHandler.sendEmptyMessage(FIND_ELEMENT)
        }

        override fun onFinish() {
            onTimerFinish()
        }
    }

    private fun getQueueElement(position: Int): ElementBean? {
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
}