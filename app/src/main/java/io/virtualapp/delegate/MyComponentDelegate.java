package io.virtualapp.delegate;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.cmprocess.ipc.VCore;
import com.cmprocess.ipc.event.EventCallback;
import com.lody.virtual.client.hook.delegate.ComponentDelegate;
import io.virtualapp.hook.HookTest;
import java.lang.reflect.Method;
import lab.galaxy.yahfa.HookMain;
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

        try {
            backupAndHook(Class.forName("com.hxy.app.aidlserver.MainActivity", true, application.getClassLoader()), "test",
                    HookTest.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void backupAndHook(Class targetClazz, String methodName, Class hookClazz) {
        try {
            Method hook = null;
            Method backup = null;

            Method[] declaredMethods = hookClazz.getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (method.getName().equals("hook")) {
                    hook = method;
                } else if (method.getName().equals("backup")) {
                    backup = method;
                }
            }

            if (hook == null) {
                Log.e(TAG, "Cannot find hook for " + methodName);
                return;
            }

            HookMain.backupAndHook(targetClazz.getDeclaredMethod(methodName, String.class),
                    hook,
                    backup);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    }
}
