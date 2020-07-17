package com.cheng.automate.core.jobs

import com.cheng.automate.core.base.BaseAccessibilityJob

/**
 * @author zijian.cheng
 * @date 2020/7/16
 */
class ShotOnVideo : BaseAccessibilityJob(null) {

    companion object {
        val instance: ShotOnVideo by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ShotOnVideo()
        }
    }

    override fun onWorking() {

    }
}