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

   private EventCallback mEventCallback = new EventCallback() {
        @Override
        public void onEventCallBack(Bundle event) {
            //main thread
            final int type = event.getInt(VEnv.INTENT_EXT_TYPE, Type.TYPE_UNKNOWN);
            Log.d("MyComponentDelegate", "type: " + type);
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
//        ueToolActivityLifecycleCallbacks.onActivityCreated(activity, null);
    }

    @Override
    public void afterActivityResume(Activity activity) {
//        ueToolActivityLifecycleCallbacks.onActivityResumed(activity);
    }

    @Override
    public void afterActivityPause(Activity activity) {
//        ueToolActivityLifecycleCallbacks.onActivityPaused(activity);
    }

    @Override
    public void afterActivityDestroy(Activity activity) {
//        ueToolActivityLifecycleCallbacks.onActivityDestroyed(activity);
    }

    @Override
    public void onSendBroadcast(Intent intent) {
        Log.d("UEToolBroadcastReceiver", " onSendBroadcast ");

    }
}
