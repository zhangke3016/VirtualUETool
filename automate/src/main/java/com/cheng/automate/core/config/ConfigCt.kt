package com.cheng.automate.core.config

/**
 * @author zijian.cheng
 * @date 2020/7/16
 */
object ConfigCt {
    const val DEBUG = true //调试标识：

    //本地目录：
    @Volatile
    var LocalDir = "" //本地工作目录；

    @Volatile
    var LocalPath = "" //本地工作路径；

    @JvmField
    @Volatile
    var AppName = "" //本app名称；

    const val TAG = "byc001" //调试标识：
    const val TAG2 = "byc002" //调试标识：
    const val appID = "ct" //定义app标识：

}