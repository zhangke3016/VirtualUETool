package com.cmprocess.ipc;


import com.cmprocess.ipc.client.core.VirtualCore;
import com.cmprocess.ipc.client.ipc.ServiceManagerNative;
import com.cmprocess.ipc.event.EventCallback;
import com.cmprocess.ipc.event.EventCenter;
import com.cmprocess.ipc.helper.ipcbus.IPCBus;
import com.cmprocess.ipc.helper.utils.AppUtil;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;

/**
 * @author zk
 * @date 创建时间：2019/2/11
 * @Description： Cross-process communication core tool class.
 * @other 修改历史：
 */
public class VCore {

    private static final VCore V_CORE = new VCore();

    public static void init(Context context, String packageName){
        VirtualCore.get().startup(context, packageName);
    }

    public static void init(Context context){
        VirtualCore.get().startup(context);
    }

    public static VCore getCore(){
        return V_CORE;
    }

    /**
     * Register service
     * @param interfaceClass
     * @param server
     * @return
     */
    public VCore registerService(Class<?> interfaceClass, Object server){

        if (VirtualCore.get().getContext() == null){
            return this;
        }
        Object o = IPCBus.getLocalService(interfaceClass);
        IBinder service = ServiceManagerNative.getService(interfaceClass.getName());
        if (service != null && o != null){
            return this;
        }
        IPCBus.registerLocal(interfaceClass,server);
        IPCBus.register(interfaceClass,server);
        return this;
    }

    /**
     * Unregister service
     * @param interfaceClass
     * @return
     */
    public VCore unregisterService(Class<?> interfaceClass){
        IPCBus.unregisterLocal(interfaceClass);
        IPCBus.removeService(interfaceClass.getName());
        return this;
    }

    /**
     * Get service
     * @param ipcClass
     * @param <T>
     * @return
     */
    public <T> T getService(Class<T> ipcClass){
        T localService = IPCBus.getLocalService(ipcClass);
        if (localService != null){
            return localService;
        }
        return VManager.get().getService(ipcClass);
    }

    /**
     * Register local service
     * @param interfaceClass
     * @param server
     * @return
     */
    public VCore registerLocalService(Class<?> interfaceClass, Object server){
        Object o = IPCBus.getLocalService(interfaceClass);
        if (o != null){
            return this;
        }
        IPCBus.registerLocal(interfaceClass,server);
        return this;
    }

    /**
     * Unregister local service
     * @param interfaceClass
     * @return
     */
    public VCore unregisterLocalService(Class<?> interfaceClass){
        IPCBus.unregisterLocal(interfaceClass);
        return this;
    }

    /**
     * Get local service
     * @param ipcClass
     * @param <T>
     * @return
     */
    public <T> T getLocalService(Class<T> ipcClass){
        T localService = IPCBus.getLocalService(ipcClass);
        return localService;
    }

    /**
     * Subscription listener
     * @param key
     * @param eventCallback
     */
    public void subscribe(String key, EventCallback eventCallback){
        EventCenter.subscribe(key,eventCallback);
    }

    /**
     * Remove determined event listeners
     * @param eventCallback
     */
    public void unsubscribe(EventCallback eventCallback){
        EventCenter.unsubscribe(eventCallback);
    }

    /**
     * Remove key all event callback listeners
     * @param key
     */
    public void unsubscribe(String key){
        EventCenter.unsubscribe(key);
    }

    /**
     * send data
     * @param key
     * @param event
     */
    public void post(String key,Bundle event){
        IPCBus.post(key,event);
    }


}
