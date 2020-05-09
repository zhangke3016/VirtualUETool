// IPCCallback.aidl
package com.cmprocess.ipc.server;

// Declare any non-default types here with import statements

interface IPCCallback {
    void onSuccess(in Bundle result);
    void onFail(String reason);
}
