package io.virtualapp.delegate;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.lody.virtual.client.hook.delegate.ComponentDelegate;

import java.io.File;

import me.ele.uetool.VEnv;
import me.ele.uetool.MeasureToolHelper;
import me.ele.uetool.UETMenu;
import me.ele.uetool.UETool;


public class MyComponentDelegate implements ComponentDelegate {

    Handler mHandler = new Handler(Looper.getMainLooper());

    SDCardListener mSDCardListener = new SDCardListener(VEnv.DIR);

    private int visibleActivityCount;

    @Override
    public void beforeApplicationCreate(Application application) {

    }

    @Override
    public void afterApplicationCreate(Application application) {

        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                try{
                    if (activity instanceof FragmentActivity){
                        ((FragmentActivity)activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(
                                new FragmentManager.FragmentLifecycleCallbacks() {
                                    @Override
                                    public void onFragmentStopped(FragmentManager fm, Fragment f) {
                                        super.onFragmentStopped(fm, f);
                                        UETMenu.dismiss(activity);
                                    }
                                }, true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                visibleActivityCount++;
                if (visibleActivityCount == 1){
                    stopWatch();
                    if (mSDCardListener == null){
                        mSDCardListener = new SDCardListener(VEnv.DIR);
                    }
                    startWatch();
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                visibleActivityCount--;
                if (visibleActivityCount == 0 && mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                if (visibleActivityCount == 0 && mSDCardListener != null){
                    stopWatch();
                }
                UETMenu.dismiss(activity);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public class SDCardListener extends FileObserver{

        public SDCardListener(String path){
            super(path, FileObserver.CREATE);
        }

        @Override
        public void onEvent(int event, @Nullable String path) {
            switch(event){
                case FileObserver.CREATE:
                    new File(VEnv.DIR + "/"
                            + MeasureToolHelper.Type.TYPE_EDIT_ATTR).delete();
                    new File(VEnv.DIR + "/"
                            + MeasureToolHelper.Type.TYPE_RELATIVE_POSITION).delete();
                    new File(VEnv.DIR + "/"
                            + MeasureToolHelper.Type.TYPE_SHOW_GRIDDING).delete();
                    if (path.equals(MeasureToolHelper.Type.TYPE_EDIT_ATTR+"")){
                        mHandler.post(() -> UETMenu.open(MeasureToolHelper.Type.TYPE_EDIT_ATTR));
                    }else if (path.equals(MeasureToolHelper.Type.TYPE_RELATIVE_POSITION+"")){
                        mHandler.post(() -> UETMenu.open(MeasureToolHelper.Type.TYPE_RELATIVE_POSITION));
                    }else if (path.equals(MeasureToolHelper.Type.TYPE_SHOW_GRIDDING+"")){
                        mHandler.post(() -> UETMenu.open(MeasureToolHelper.Type.TYPE_SHOW_GRIDDING));
                    }
                    break;
            }
        }
    }

    private void startWatch(){
        mSDCardListener.startWatching();
    }

    private void stopWatch(){
        mSDCardListener.stopWatching();
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
        UETool.setTargetActivity(activity);
    }

    @Override
    public void afterActivityResume(Activity activity) {
        UETool.setTargetActivity(activity);
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
