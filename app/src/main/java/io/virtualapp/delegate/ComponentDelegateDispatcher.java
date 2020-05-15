package io.virtualapp.delegate;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import com.lody.virtual.client.hook.delegate.ComponentDelegate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComponentDelegateDispatcher implements ComponentDelegate {

    private final List<ComponentDelegate> mComponentDelegates;

    public ComponentDelegateDispatcher(ComponentDelegate... componentDelegates) {
        if (componentDelegates == null) {
            mComponentDelegates = new ArrayList<>();
        } else {
            mComponentDelegates = Arrays.asList(componentDelegates);
        }
    }

    @Override
    public void beforeApplicationCreate(Application application) {
        for (ComponentDelegate componentDelegate: mComponentDelegates) {
            componentDelegate.beforeApplicationCreate(application);
        }
    }

    @Override
    public void afterApplicationCreate(Application application) {
        for (ComponentDelegate componentDelegate: mComponentDelegates) {
            componentDelegate.afterApplicationCreate(application);
        }
    }

    @Override
    public void beforeActivityCreate(Activity activity) {
        for (ComponentDelegate componentDelegate: mComponentDelegates) {
            componentDelegate.beforeActivityCreate(activity);
        }
    }

    @Override
    public void beforeActivityResume(Activity activity) {
        for (ComponentDelegate componentDelegate: mComponentDelegates) {
            componentDelegate.beforeActivityResume(activity);
        }
    }

    @Override
    public void beforeActivityPause(Activity activity) {
        for (ComponentDelegate componentDelegate: mComponentDelegates) {
            componentDelegate.beforeActivityPause(activity);
        }
    }

    @Override
    public void beforeActivityDestroy(Activity activity) {
        for (ComponentDelegate componentDelegate: mComponentDelegates) {
            componentDelegate.beforeActivityDestroy(activity);
        }
    }

    @Override
    public void afterActivityCreate(Activity activity) {
        for (ComponentDelegate componentDelegate: mComponentDelegates) {
            componentDelegate.afterActivityCreate(activity);
        }
    }

    @Override
    public void afterActivityResume(Activity activity) {
        for (ComponentDelegate componentDelegate: mComponentDelegates) {
            componentDelegate.afterActivityResume(activity);
        }
    }

    @Override
    public void afterActivityPause(Activity activity) {
        for (ComponentDelegate componentDelegate: mComponentDelegates) {
            componentDelegate.afterActivityPause(activity);
        }
    }

    @Override
    public void afterActivityDestroy(Activity activity) {
        for (ComponentDelegate componentDelegate: mComponentDelegates) {
            componentDelegate.afterActivityDestroy(activity);
        }
    }

    @Override
    public void onSendBroadcast(Intent intent) {
        for (ComponentDelegate componentDelegate: mComponentDelegates) {
            componentDelegate.onSendBroadcast(intent);
        }
    }
}
