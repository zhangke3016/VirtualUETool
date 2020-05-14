/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/weishu/dev/github/VirtualXposed/VirtualApp/lib/src/main/aidl/com/lody/virtual/server/IJobScheduler.aidl
 */
package com.lody.virtual.server;

import android.app.job.JobInfo;
import android.app.job.JobWorkItem;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import java.util.List;

/**
 * IPC interface that supports the app-facing {@link android.app.job.JobScheduler} api.
 */
public interface IJobScheduler extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements IJobScheduler {
        private static final String DESCRIPTOR = "com.lody.virtual.server.IJobScheduler";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.lody.virtual.server.IJobScheduler interface,
         * generating a proxy if needed.
         */
        public static IJobScheduler asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof IJobScheduler))) {
                return ((IJobScheduler) iin);
            }
            return new Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_schedule: {
                    data.enforceInterface(DESCRIPTOR);
                    JobInfo _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = JobInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    int _result = this.schedule(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_cancel: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    this.cancel(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_cancelAll: {
                    data.enforceInterface(DESCRIPTOR);
                    this.cancelAll();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_getAllPendingJobs: {
                    data.enforceInterface(DESCRIPTOR);
                    List<JobInfo> _result = this.getAllPendingJobs();
                    reply.writeNoException();
                    reply.writeTypedList(_result);
                    return true;
                }
                case TRANSACTION_enqueue: {
                    data.enforceInterface(DESCRIPTOR);
                    JobInfo _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = JobInfo.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    JobWorkItem _arg1;
                    if ((0 != data.readInt())) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            _arg1 = JobWorkItem.CREATOR.createFromParcel(data);
                        } else {
                            _arg1 = null;
                        }
                    } else {
                        _arg1 = null;
                    }
                    int _result = this.enqueue(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_getPendingJob: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    JobInfo _result = this.getPendingJob(_arg0);
                    reply.writeNoException();
                    if ((_result != null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IJobScheduler {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public int schedule(JobInfo job) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((job != null)) {
                        _data.writeInt(1);
                        job.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_schedule, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void cancel(int jobId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(jobId);
                    mRemote.transact(Stub.TRANSACTION_cancel, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void cancelAll() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_cancelAll, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public List<JobInfo> getAllPendingJobs() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                List<JobInfo> _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getAllPendingJobs, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.createTypedArrayList(JobInfo.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public int enqueue(JobInfo job, JobWorkItem work) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((job != null)) {
                        _data.writeInt(1);
                        job.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if ((work != null)) {
                        _data.writeInt(1);
                        work.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_enqueue, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public JobInfo getPendingJob(int i) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                JobInfo _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(i);
                    mRemote.transact(Stub.TRANSACTION_getPendingJob, _data, _reply, 0);
                    _reply.readException();
                    if ((0 != _reply.readInt())) {
                        _result = JobInfo.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
        }

        static final int TRANSACTION_schedule = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_cancel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_cancelAll = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_getAllPendingJobs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
        static final int TRANSACTION_enqueue = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
        static final int TRANSACTION_getPendingJob = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
    }

    public int schedule(JobInfo job) throws RemoteException;

    public void cancel(int jobId) throws RemoteException;

    public void cancelAll() throws RemoteException;

    public List<JobInfo> getAllPendingJobs() throws RemoteException;

    public int enqueue(JobInfo job, JobWorkItem work) throws RemoteException;

    public JobInfo getPendingJob(int i) throws RemoteException;
}