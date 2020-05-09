package com.cmprocess.ipc.helper.ipcbus;


/**
 * @author zk
 * @date 创建时间：2019/2/1
 * @Description：
 * @other 修改历史：
 */
public class IPCSingleton<T> {

    private Class<?> ipcClass;
    private T instance;

    public IPCSingleton(Class<?> ipcClass) {
        this.ipcClass = ipcClass;
    }

    public T get() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = IPCBus.get(ipcClass);
                }
            }
        }
        return instance;
    }

}
