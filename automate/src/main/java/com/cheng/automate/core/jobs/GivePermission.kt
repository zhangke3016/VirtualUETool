package com.cheng.automate.core.jobs

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.cheng.automate.core.base.BaseAccessibilityJob
import com.cheng.automate.core.helper.AccessibilityHelper

/**
 * @author zijian.cheng
 * @date 2020/7/16
 */
class GivePermission : BaseAccessibilityJob(null) {

    var mKeyWords: Array<String>? = null

    companion object {
        val instance: GivePermission by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GivePermission()
        }
    }

    override fun onWorking() {
        if (service == null) return
        val rootNode = service!!.rootInActiveWindow ?: return
        //recycleGiveNotification(event);
        recycleClick(rootNode)
    }

    override fun onReceiveJob(event: AccessibilityEvent?) {
        super.onReceiveJob(event)
        if (!mIsEventWorking) return
        if (!mIsTargetPackageName) return
        val eventType = event!!.eventType
        if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            var nodeInfo = event.source ?: return
            nodeInfo = AccessibilityHelper.getRootNode(nodeInfo) ?: return
            recycleClick(nodeInfo)
        }
    }

    private fun recycleClick(rootNode: AccessibilityNodeInfo) {
        recycleGiveCheck(rootNode)
        recycleGiveButton(rootNode)
        //closeTimeWorking();
    }

    //-----------------------------------------------------------------------------------------------
    private fun recycleGiveCheck(info: AccessibilityNodeInfo) {
        if (info.childCount == 0) {
            //取信息
            if (info.className == null) return
            val className = info.className.toString()
            if (className == AccessibilityHelper.WIDGET_CHECKBOX && info.isCheckable) {
                if (!info.isChecked) if (info.isClickable) info.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        } else {
            for (i in 0 until info.childCount) {
                if (info.getChild(i) != null) {
                    recycleGiveCheck(info.getChild(i))
                }
            }
        }
    }

    private fun recycleGiveButton(info: AccessibilityNodeInfo) {
        if (info.childCount == 0) {
            //取信息
            if (info.className == null) return
            val className = info.className.toString()
            if (className == AccessibilityHelper.WIDGET_TEXT && info.isClickable) {
                if (info.text == null) return
                val txtTxt = info.text.toString()
                //if(txtTxt.equals("重新登录"))info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return
            }
            if (className == AccessibilityHelper.WIDGET_BUTTON && info.isClickable) {
                if (info.text == null) return
                val btnTxt = info.text.toString()
                if (btnTxt.contains("授权")) info.performAction(AccessibilityNodeInfo.ACTION_CLICK) //授权：superU；启动：三星锁屏
                if (btnTxt.contains("允许")) info.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                //if(btnTxt.contains("安装"))info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                //if(btnTxt.contains("发送"))info.performAction(AccessibilityNodeInfo.ACTION_CLICK);//oppo
                if (btnTxt.indexOf("激活") != -1) info.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                if (btnTxt.indexOf("立即开始") != -1) info.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                //if(btnTxt.indexOf("仍然支付")!=-1)info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                //if(btnTxt.contains("允许一次"))info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                clickKeyWords(btnTxt, info)
            }
        } else {
            for (i in 0 until info.childCount) {
                if (info.getChild(i) != null) {
                    recycleGiveButton(info.getChild(i))
                }
            }
        }
    }

    /**
     * 点击关键字
     */
    private fun clickKeyWords(btnTxt: String, info: AccessibilityNodeInfo) {
        if (mKeyWords == null || mKeyWords!!.isEmpty()) return
        for (key in mKeyWords!!) {
            //三星锁屏
            if (btnTxt.contains(key)) info.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }

    override fun onEventStart() {
        super.onEventStart()
        val runnableEvent = Runnable {
            closeEventWorking()
            mKeyWords = null
        }
        mHandler.postDelayed(runnableEvent, TIME_WORKING_CONTINUE.toLong())
    }
}