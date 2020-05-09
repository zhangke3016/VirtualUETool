package com.cmprocess.ipc.event;

import com.cmprocess.ipc.server.IEventReceiver;

import android.os.Bundle;
import android.os.Parcelable;

/**
 * @author zk
 * @date 创建时间：2019/2/15
 * @Description：
 * @other 修改历史：
 */
public class EventReceiver extends IEventReceiver.Stub {

    private static final EventReceiver EVENT_RECEIVER = new EventReceiver();

    private EventReceiver(){}

    public static final EventReceiver getInstance(){
        return EVENT_RECEIVER;
    }

    @Override
    public void onEventReceive(String key,Bundle event) {
        EventCenter.onEventReceive(key,event);
    }
}
