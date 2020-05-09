package com.cmprocess.ipc.helper.ipcbus;

import android.os.IBinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author zk
 * @date 创建时间：2019/2/1
 * @Description：
 * @other 修改历史：
 */
public class IPCInvocationBridge implements InvocationHandler {

    private ServerInterface serverInterface;
    private IBinder binder;

    public IPCInvocationBridge(ServerInterface serverInterface, IBinder binder) {
        this.serverInterface = serverInterface;
        this.binder = binder;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        IPCMethod ipcMethod = serverInterface.getIPCMethod(method);
        if (ipcMethod == null) {
            throw new IllegalStateException("Can not found the ipc method : " + method.getDeclaringClass().getName() + "@" +  method.getName());
        }
        return ipcMethod.callRemote(binder, args);
    }
}