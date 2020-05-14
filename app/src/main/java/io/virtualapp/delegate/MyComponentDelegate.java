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
import io.virtualapp.hook.HookUtils;
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
            Log.d(TAG, "onEventCallBack type: " + type);
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
        //Hook test
        HookUtils.backupAndHook("com.hxy.app.aidlserver.MainActivity", "test", application.getClassLoader(), getClass().getClassLoader(),
                "io.virtualapp.hook.HookTest", String.class);
        //Hook setOnClickListener
        HookUtils.backupAndHook(View.class, "setOnClickListener", getClass().getClassLoader(),
                "io.virtualapp.hook.HookSetOnClickListener", OnClickListener.class);
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
