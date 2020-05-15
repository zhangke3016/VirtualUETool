package io.virtual.magictool.hook;

import static io.virtual.magictool.OkHttpHook.globalInterceptors;
import static io.virtual.magictool.OkHttpHook.globalNetworkInterceptors;
import static io.virtual.magictool.OkHttpHook.installInterceptor;

import android.util.Log;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class NetworkHook {

    public static OkHttpClient hook(Object object) {
        Log.w("NetworkHook", "hook build: ");
        installInterceptor();
        performOkhttpOneParamBuilderInit(object);
        return backup(object);
    }

    public static OkHttpClient backup(Object object) {
        Log.w("NetworkHook", "hook build should not be here");
        return null;
    }

    public static String getHookClassName() {
        return "okhttp3.OkHttpClient.Builder";
    }

    public static String getClassName() {
        return "io.virtual.magictool.hook.NetworkHook";
    }

    public static String getHookMethodName() {
        return "build";
    }

    /**
     * @param builder 真实的对象为okHttpClient.Builder
     */
    public static void performOkhttpOneParamBuilderInit(Object builder) {
        try {
            if (builder instanceof OkHttpClient.Builder) {
                OkHttpClient.Builder localBuild = (OkHttpClient.Builder) builder;
                for (Interceptor interceptor : globalInterceptors) {
                    localBuild.addInterceptor(interceptor);
                }
                for (Interceptor interceptor : globalNetworkInterceptors) {
                    localBuild.addNetworkInterceptor(interceptor);
                }
//                List<Interceptor> interceptors = removeDuplicate(localBuild.interceptors());
//                List<Interceptor> networkInterceptors = removeDuplicate(localBuild.networkInterceptors());
//                ReflectUtils.reflect(localBuild).field("interceptors", interceptors);
//                ReflectUtils.reflect(localBuild).field("networkInterceptors", networkInterceptors);
            }
        } catch (Exception e) {
            Log.i("Doraemon", "" + e.getMessage());
        }

    }
}
