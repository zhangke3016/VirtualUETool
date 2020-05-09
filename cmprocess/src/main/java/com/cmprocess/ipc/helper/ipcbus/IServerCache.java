package com.cmprocess.ipc.helper.ipcbus;

import android.os.Bundle;
import android.os.IBinder;

/**
 * @author zk
 * @date 创建时间：2019/2/1
 * @Description：
 * @other 修改历史：
 */
public interface IServerCache {
    void join(String serverName, IBinder binder);
    void joinLocal(String serverName, Object object);
    void removeService(String serverName);
    void removeLocalService(String serverName);
    IBinder query(String serverName);
    Object queryLocal(String serverName);
    void post(String key,Bundle bundle);
}
