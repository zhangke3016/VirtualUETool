package com.cmprocess.ipc.client.ipc;

import android.util.Log;
import com.cmprocess.ipc.client.core.VirtualCore;
import com.cmprocess.ipc.helper.compat.BundleCompat;
import com.cmprocess.ipc.server.IServiceFetcher;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * @author zk
 * @date 创建时间：2019/2/1
 * @Description：
 * @other 修改历史：
 */
public class ServiceManagerNative {

    public static final String SERVICE_DEF_AUTH = "cmprocess.service.BinderProvider";
    private static final String TAG = ServiceManagerNative.class.getSimpleName();
    public static String SERVICE_CP_AUTH = "cmprocess.service.BinderProvider";

    private static IServiceFetcher sFetcher;

    private static IServiceFetcher getServiceFetcher() {
        if (sFetcher == null || !sFetcher.asBinder().isBinderAlive()) {
            synchronized (ServiceManagerNative.class) {
                Context context = VirtualCore.get().getContext();
                Bundle response = new ProviderCall.Builder(context, SERVICE_CP_AUTH).methodName("@").call();
                if (response != null) {
                    IBinder binder = BundleCompat.getBinder(response, "_VM_|_binder_");
                    linkBinderDied(binder);
                    sFetcher = IServiceFetcher.Stub.asInterface(binder);
                    Log.d(TAG, "packageName:  "+ context.getPackageName() + "  sFetcher:  " + (sFetcher == null));
                }
            }
        }
        return sFetcher;
    }

    public static IBinder getService(String name) {
        IServiceFetcher fetcher = getServiceFetcher();
        if (fetcher != null) {
            try {
                return fetcher.getService(name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void addService(String name, IBinder service) {
        IServiceFetcher fetcher = getServiceFetcher();
        if (fetcher != null) {
            try {
                fetcher.addService(name, service);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addEventListener(String name, IBinder service) {
        IServiceFetcher fetcher = getServiceFetcher();
        if (fetcher != null) {
            try {
                fetcher.addEventListener(name, service);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void post(String key,Bundle bundle) {
        IServiceFetcher fetcher = getServiceFetcher();
        if (fetcher != null) {
            try {
                fetcher.post(key,bundle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeService(String name) {
        IServiceFetcher fetcher = getServiceFetcher();
        if (fetcher != null) {
            try {
                fetcher.removeService(name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private static void linkBinderDied(final IBinder binder) {
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                binder.unlinkToDeath(this, 0);
            }
        };
        try {
            binder.linkToDeath(deathRecipient, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
