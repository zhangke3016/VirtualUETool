package com.cheng.automate.core.jobs

import android.view.accessibility.AccessibilityEvent
import com.cheng.automate.core.AutoAccessibilityService
import com.cheng.automate.core.base.BaseAccessibilityJob
import me.ele.uetool.base.config.Order

/**
 * @author zijian.cheng
 * @date 2020/7/16
 */
class ManageAccessibilityJob : BaseAccessibilityJob(null) {

    private val mGivePermission: GivePermission = GivePermission.instance
    private val mProtectMe: ProtectMe = ProtectMe.instance
    private val mSaveNotification: AccessibilitySaveNotification = AccessibilitySaveNotification.instance
    private val mShotOnVideo: ShotOnVideo = ShotOnVideo.instance
    private val mExeClick: ExeClick = ExeClick.getInstance(Order.CMD_POS)
    private val mFlowClick: FlowClick = FlowClick.instance

    companion object {
        val instance: ManageAccessibilityJob by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ManageAccessibilityJob()
        }
    }

    override fun onCreateJob(service: AutoAccessibilityService?) {
        super.onCreateJob(service)
        onEventStart()
//        mGivePermission.onCreateJob(service)
//        mProtectMe.onCreateJob(service)
//        mSaveNotification.onCreateJob(service)
//        mShotOnVideo.onCreateJob(service)
//        mExeClick.onCreateJob(service)
//        mFlowClick.onCreateJob(service)
    }

    override fun onStopJob() {
        super.onStopJob()
//        mGivePermission.onStopJob()
//        mProtectMe.onStopJob()
//        mSaveNotification.onStopJob()
//        mShotOnVideo.onStopJob()
//        mExeClick.onStopJob()
//        mFlowClick.onStopJob()
    }

    override fun onReceiveJob(event: AccessibilityEvent?) {
        super.onReceiveJob(event)
        if (!mIsEventWorking) return
        if (!mIsTargetPackageName) return
        //debug(event);
//        mGivePermission.onReceiveJob(event)
//        mProtectMe.onReceiveJob(event)
//        mFlowClick.onReceiveJob(event)
//        mSaveNotification.onReceiveJob(event)
//        mShotOnVideo.onReceiveJob(event)
    }

    override fun onWorking() {

    }
}