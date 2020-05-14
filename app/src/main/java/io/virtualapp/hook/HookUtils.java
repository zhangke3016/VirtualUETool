package io.virtualapp.hook;

import android.util.Log;
import java.lang.reflect.Method;
import lab.galaxy.yahfa.HookMain;

public class HookUtils {

    public static void backupAndHook(String clazzName, String methodName, ClassLoader patchClassLoader, ClassLoader originClassLoader, String hookClazzName, Class<?>... parameterTypes) {
        try {
            Class targetClazz = Class.forName(clazzName, true, patchClassLoader);
            backupAndHook(targetClazz, methodName, originClassLoader, hookClazzName, parameterTypes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void backupAndHook(Class targetClazz, String methodName, ClassLoader originClassLoader, String hookClazzName, Class<?>... parameterTypes) {
        try {
            Class hookClazz = Class.forName(hookClazzName, true, originClassLoader);
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
                Log.e("HookUtils", "Cannot find hook for " + methodName);
                return;
            }

            HookMain.backupAndHook(targetClazz.getDeclaredMethod(methodName, parameterTypes),
                    hook,
                    backup);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printStackTrace() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder(512);
        for (StackTraceElement element : elements) {
            sb.append(element.toString()).append("\n");
        }
        Log.d("HookUtils", "StackTrace: \n:" + sb.toString());
    }
}
