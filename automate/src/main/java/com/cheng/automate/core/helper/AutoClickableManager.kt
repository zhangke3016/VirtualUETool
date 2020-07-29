package com.cheng.automate.core.helper

import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.cheng.automate.core.AutoAccessibilityService
import me.ele.uetool.base.db.ElementBean
import me.ele.uetool.base.db.MMKVUtil

/**
 * @author zijian.cheng
 * @date 2020/7/22
 */
object AutoClickableManager {

    const val millisInFuture: Long = 10000 //寻找节点总时长
    const val countDownInterval: Long = 500 //寻找节点间隔时间
    const val STEP_NEXT = 0x00
    const val STEP_DELAY = 0x01
    const val FIND_ELEMENT = 0x02

    private var mDownTimer: DownTimer = DownTimer()
    private val mClickStepHandler: ClickStepHandler = ClickStepHandler(Looper.getMainLooper())
    private var elementList: MutableList<ElementBean?>? = null
    private const val loop = true

    private var currentOrders: Array<out ElementBean?>? = null
    private var index = 0

    class ClickStepHandler(mainLooper: Looper) : Handler(mainLooper) {
        override fun handleMessage(msg: Message) {
            if (msg.what == STEP_NEXT) {
                val elementBean = getQueueElement(index)
                elementBean?.let {
                    mClickStepHandler.removeCallbacksAndMessages(null)
                    mClickStepHandler.sendMessageDelayed(Message.obtain(mClickStepHandler, STEP_DELAY, it), it.stepDelay)
                }
            } else if (msg.what == STEP_DELAY) {
                if (msg.obj is ElementBean) {
                    findNodeInfoByTimer(msg.obj as ElementBean)
                }
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
        elementList = MMKVUtil.getInstance().elements
        if (elementList.isNullOrEmpty()) {
            return
        }
        mClickStepHandler.removeCallbacksAndMessages(null)
        mClickStepHandler.sendEmptyMessage(STEP_NEXT)
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
        var sendDelayed = 50L
        if (nodeInfo == null) {
            //only click
            index += 1
            AccessibilityHelper.performClickByRect(element.rect)
        } else {
            if (!element.text.isNullOrEmpty() && nodeInfo.text != element.text) {
                AccessibilityHelper.nodeInput(nodeInfo, element.text)
            }
            index += 1
            if (element.scrollDuration != 0L) {
                //有滑动时间，说明是滑动手势
                AccessibilityHelper.processSwipe(element.fromPoint, element.toPoint, element.scrollDuration)
                sendDelayed = element.scrollDuration
            } else {
                AccessibilityHelper.performClick(nodeInfo)
            }
        }
        mClickStepHandler.removeCallbacksAndMessages(null)
        mClickStepHandler.sendEmptyMessageDelayed(STEP_NEXT, sendDelayed)
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
        return elementList?.let {
            when {
                position < it.size -> {
                    it[position]
                }
                loop -> {
                    index = 0
                    it[0]
                }
                else -> {
                    null
                }
            }
        }
    }
}