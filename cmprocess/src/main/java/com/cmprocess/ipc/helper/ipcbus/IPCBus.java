package com.cmprocess.ipc.helper.ipcbus;

import android.os.Bundle;
import android.os.IBinder;

import java.lang.reflect.Proxy;

/**
 * @author zk
 * @date 创建时间：2019/2/1
 * @Description：
 * @other 修改历史：
 */
public class IPCBus {

    private static IServerCache sCache;

    public static void initialize(IServerCache cache) {
        sCache = cache;
    }

    private static void checkInitialized() {
        if (sCache == null) {
            throw new IllegalStateException("please call initialize() at first.");
        }
    }

    public static void register(Class<?> interfaceClass, Object server) {
        checkInitialized();
        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        TransformBinder binder = new TransformBinder(serverInterface, server);
        sCache.join(serverInterface.getInterfaceName(), binder);
    }

    public static void removeService(String serverName) {
        checkInitialized();
        sCache.removeService(serverName);
    }

    public static <T> T get(Class<?> interfaceClass) {
        checkInitialized();
        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        IBinder binder = sCache.query(serverInterface.getInterfaceName());
        if (binder == null) {
            return null;
        }
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new IPCInvocationBridge(serverInterface, binder));
    }


    public static void registerLocal(Class<?> interfaceClass, Object server) {
        checkInitialized();
        sCache.joinLocal(interfaceClass.getName(), server);
    }

    public static void unregisterLocal(Class<?> interfaceClass) {
        checkInitialized();
        sCache.removeLocalService(interfaceClass.getName());
    }

    public static <T> T getLocalService(Class<T> interfaceClass) {
       checkInitialized();
       return (T) sCache.queryLocal(interfaceClass.getName());
    }

    public static void post(String key,Bundle event) {
        checkInitialized();
        sCache.post(key,event);
    }
}
