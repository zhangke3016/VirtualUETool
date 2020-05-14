package io.virtualapp.delegate;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.cmprocess.ipc.VCore;
import com.cmprocess.ipc.event.EventCallback;
import com.lody.virtual.client.hook.delegate.ComponentDelegate;
import io.virtualapp.bridge.DexposedBridge;
import io.virtualapp.bridge.DexposedHelper;
import io.virtualapp.bridge.XC_MethodHook;
import io.virtualapp.hook.HookOnClickListener;
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
        DexposedHelper.init(application);
        VCore.getCore().subscribe(VEnv.ACTION_UETOOL, mEventCallback);
        //Hook test
        DexposedHelper.findAndHookMethod("com.hxy.app.aidlserver.MainActivity", application.getClassLoader(), "test", String.class, new XC_MethodHook() {
            @Override
            public void beforeHookedMethod(MethodHookParams params) {
                params.args[0] = params.args[0] + "hook!!!";
            }
        });

        //Hook setOnClickListener
        DexposedHelper.findAndHookMethod(View.class, "setOnClickListener", OnClickListener.class, new XC_MethodHook() {
            @Override
            public void beforeHookedMethod(MethodHookParams params) {
                params.args[0] = new HookOnClickListener((OnClickListener) params.args[0]);
                Log.d(TAG, "beforeHookedMethod ");
            }

            @Override
            public void afterHookedMethod(MethodHookParams params) {
                Log.d(TAG, " afterHookedMethod ");
            }
        });
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
