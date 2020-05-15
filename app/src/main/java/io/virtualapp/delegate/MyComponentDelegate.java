package io.virtualapp.delegate;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.cmprocess.ipc.VCore;
import com.cmprocess.ipc.event.EventCallback;
import com.lody.virtual.client.hook.delegate.ComponentDelegate;
import me.ele.uetool.MenuHelper.Type;
import me.ele.uetool.UETMenu;
import me.ele.uetool.VEnv;


public class MyComponentDelegate implements ComponentDelegate {

    private static final String TAG = "MyComponentDelegate";

    private EventCallback mEventCallback = new EventCallback() {
        @Override
        public void onEventCallBack(Bundle event) {
            //main thread
            final int type = event.getInt(VEnv.INTENT_EXT_TYPE, Type.TYPE_UNKNOWN);
            Log.d(TAG, " onEventCallBack type: " + type);
            if (type != Type.TYPE_UNKNOWN) {
                UETMenu.open(type);
            }
        }
    };

    @Override
    public void beforeApplicationCreate(Application application) {

    }

    @Override
    public void afterApplicationCreate(Application application) {
        VCore.init(application, "io.virtualapp268");
        VCore.getCore().subscribe(VEnv.ACTION_UETOOL, mEventCallback);
//        DexposedHelper.init(application);
        //Hook test
//        DexposedHelper.findAndHookMethod("com.hxy.app.aidlserver.MainActivity", application.getClassLoader(), "test", String.class, new DXC_MethodHook() {
//            @Override
//            public void beforeHookedMethod(XMethodHookParams params) {
//                params.args[0] = params.args[0] + "hook!!!";
//            }
//        });

        //Hook setOnClickListener
//        DexposedHelper.findAndHookMethod(View.class, "setOnClickListener", OnClickListener.class, new DXC_MethodHook() {
//            @Override
//            public void beforeHookedMethod(XMethodHookParams params) {
//                params.args[0] = new HookOnClickListener((OnClickListener) params.args[0]);
//            }
//        });

//        DexposedHelper.findAndHookMethod(URL.class, "openConnection", new DXC_MethodHook() {
//            @Override
//            public void beforeHookedMethod(XMethodHookParams params) {
//                //protocol, host, port
//                URL url = (URL) params.thisObject;
//                URLConnection proxy = HttpUrlConnectionProxyUtil.proxy(url);
//                params.setResult(proxy);
//                Log.d(TAG, " url： " + url.toString());
//            }
//        });
//
//        NetworkManager.get().startMonitor();
//        NetworkManager.get().setOnNetworkInfoUpdateListener(new OnNetworkInfoUpdateListener() {
//            @Override
//            public void onNetworkInfoUpdate(NetworkRecord record, boolean add) {
//                if (record.mRequest != null) {
//                    Log.d(TAG, " mRequest： " +  record.mRequest.postData);
//                }
//                Log.d(TAG, " mResponseBody： " +  record.mResponseBody);
//            }
//        });
    }

    @Override
    public void beforeActivityCreate(Activity activity) {

    }

    @Override
    public void beforeActivityResume(Activity activity) {

    }

    @Override
    public void beforeActivityPause(Activity activity) {

    }

    @Override
    public void beforeActivityDestroy(Activity activity) {

    }

    @Override
    public void afterActivityCreate(Activity activity) {
    }

    @Override
    public void afterActivityResume(Activity activity) {
    }

    @Override
    public void afterActivityPause(Activity activity) {
    }

    @Override
    public void afterActivityDestroy(Activity activity) {
    }

    @Override
    public void onSendBroadcast(Intent intent) {

    }
}
