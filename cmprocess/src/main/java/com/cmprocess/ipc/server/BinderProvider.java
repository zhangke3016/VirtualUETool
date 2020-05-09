package com.cmprocess.ipc.server;

import com.cmprocess.ipc.client.core.VirtualCore;
import com.cmprocess.ipc.helper.compat.BundleCompat;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * @author zk
 * @date 创建时间：2019/2/1
 * @Description：
 * @other 修改历史：
 */
public class BinderProvider extends ContentProvider{

    private final ServiceFetcher mServiceFetcher = new ServiceFetcher();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DaemonService.startup(context);
        if (!VirtualCore.get().isStartup()) {
            return true;
        }
        return true;
    }

    
    @Override
    public Cursor query(Uri uri,  String[] projection,String selection,
             String[] selectionArgs,  String sortOrder) {
        return null;
    }
    
    @Override
    public String getType(Uri uri) {
        return null;
    }

    
    @Override
    public Uri insert(Uri uri,  ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri,  String selection,  String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri,  ContentValues values,  String selection,
             String[] selectionArgs) {
        return 0;
    }

    
    @Override
    public Bundle call(String method,  String arg,  Bundle extras) {
        if ("@".equals(method)) {
            Bundle bundle = new Bundle();
            BundleCompat.putBinder(bundle, "_VM_|_binder_", mServiceFetcher);
            return bundle;
        }
        return null;
    }

    private class ServiceFetcher extends IServiceFetcher.Stub {
        @Override
        public IBinder getService(String name) throws RemoteException {
            if (name != null) {
                return ServiceCache.getService(name);
            }
            return null;
        }

        @Override
        public void addService(String name, IBinder service) throws RemoteException {
            if (name != null && service != null) {
                ServiceCache.addService(name, service);
            }
        }

        @Override
        public void addEventListener(String name, IBinder service) throws RemoteException {
            if (name != null && service != null) {
                ServiceCache.addEventListener(name, service);
            }
        }

        @Override
        public void removeService(String name) throws RemoteException {
            if (name != null) {
                ServiceCache.removeService(name);
            }
        }

        @Override
        public void removeEventListener(String name) throws RemoteException {
            if (name != null) {
                ServiceCache.removeEventListener(name);
            }
        }

        @Override
        public void post(String key,Bundle result) throws RemoteException {
            if (result != null){
                ServiceCache.sendEvent(key,result);
            }
        }
    }
}
