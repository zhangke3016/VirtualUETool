package com.cmprocess.ipc.event;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zk
 * @date 创建时间：2019/2/15
 * @Description： Event registry
 * @other 修改历史：
 */
public class EventCenter {

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private static ConcurrentHashMap<String, List<WeakReference<EventCallback>>> subscribers = new ConcurrentHashMap<>();

    private EventCenter() {}

    /**
     * Subscription listener
     * @param key
     * @param callback
     */
    public static synchronized void subscribe(String key, EventCallback callback) {
        List<WeakReference<EventCallback>> eventCallbacks = subscribers.get(key);
        if (eventCallbacks == null){
            eventCallbacks = new ArrayList<>(5);
        }
        eventCallbacks.add(new WeakReference<EventCallback>(callback));
        subscribers.put(key, eventCallbacks);
    }

    /**
     * Remove key all event callback listeners
     * @param key
     */
    public static synchronized void unsubscribe(String key) {
        subscribers.remove(key);
    }

    /**
     * Remove determined event listeners
     * @param callback
     */
    public static synchronized void unsubscribe(EventCallback callback) {
        for (Map.Entry<String, List<WeakReference<EventCallback>>> entry : subscribers.entrySet()) {
            List<WeakReference<EventCallback>> listeners = entry.getValue();
            for (WeakReference<EventCallback> weakRef : listeners) {
                if (callback == weakRef.get()) {
                    listeners.remove(weakRef);
                    break;
                }
            }
        }
    }

    public static synchronized void onEventReceive(String key, final Bundle event) {
        if (event == null){
            return;
        }
        if (key != null) {
            List<WeakReference<EventCallback>> messageCallbacks = subscribers.get(key);
            if (messageCallbacks != null) {
                for (int i = messageCallbacks.size() - 1; i >= 0; --i) {
                    final WeakReference<EventCallback> eventCallback = messageCallbacks.get(i);
                    final EventCallback ec = eventCallback.get();
                    if (ec != null){
                        sHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ec.onEventCallBack(event);
                            }
                        });
                    }else {
                        messageCallbacks.remove(i);
                    }
                }
            }
        }
    }
}
