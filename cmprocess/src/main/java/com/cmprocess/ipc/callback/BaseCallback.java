package com.cmprocess.ipc.callback;




import com.cmprocess.ipc.server.IPCCallback;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

/**
 * @author zk
 * @date 创建时间：2019/2/15
 * @Description：
 * @other 修改历史：
 */
public abstract class BaseCallback extends IPCCallback.Stub {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public final void onSuccess(final Bundle result) throws RemoteException {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onSucceed(result);
            }
        });
    }

    @Override
    public final void onFail(final String reason) throws RemoteException {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFailed(reason);
            }
        });
    }

    public abstract void onSucceed(Bundle result);

    public abstract void onFailed(String reason);
}
