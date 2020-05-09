// IEventReceiver.aidl
package com.cmprocess.ipc.server;

// Declare any non-default types here with import statements

interface IEventReceiver {
    void onEventReceive(String key,in Bundle event);
}
