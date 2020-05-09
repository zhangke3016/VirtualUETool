package com.cmprocess.ipc;


import com.cmprocess.ipc.helper.ipcbus.IPCBus;
import com.cmprocess.ipc.helper.ipcbus.IPCSingleton;

import android.util.ArrayMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author zk
 * @date 创建时间：2019/2/11
 * @Description：
 * @other 修改历史：
 */
final class VManager {

    private static final VManager sVM = new VManager();

    private ConcurrentHashMap<Class,IPCSingleton> mIPCSingletonArrayMap = new ConcurrentHashMap<>();

    public static VManager get() {
        return sVM;
    }

    public <T> T getService(Class<T> ipcClass) {
        T t = IPCBus.get(ipcClass);
        if (t != null){
            return t;
        }
        IPCSingleton<T> tipcSingleton = mIPCSingletonArrayMap.get(ipcClass);
        if (tipcSingleton == null){
            tipcSingleton = new IPCSingleton<>(ipcClass);
            mIPCSingletonArrayMap.put(ipcClass,tipcSingleton);
        }
        return tipcSingleton.get();
    }


}
