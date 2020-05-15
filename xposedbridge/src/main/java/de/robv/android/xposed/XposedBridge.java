package de.robv.android.xposed;

import android.util.Log;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import io.virtualapp.bridge.DXC_MethodHook;
import io.virtualapp.bridge.DexposedBridge;
import io.virtualapp.bridge.DexposedHelper;
import io.virtualapp.bridge.XLog;
import io.virtualapp.bridge.classfactory.GenedClassInfo;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class contains most of Xposed's central logic, such as initialization and callbacks used by
 * the native side. It also includes methods to add new hooks.
 */
@SuppressWarnings("JniMissingFunction")
public final class XposedBridge {
    /**
     * The system class loader which can be used to locate Android framework classes.
     * Application classes cannot be retrieved from it.
     *
     */
    public static ClassLoader BOOTCLASSLOADER = XposedBridge.class.getClassLoader();

    /**
     * @hide
     */
    public static final String TAG = "Xposed";

    /**
     * @deprecated Use {@link #getXposedVersion()} instead.
     */
    @Deprecated
    public static int XPOSED_BRIDGE_VERSION = 89;

    private static final Object[] EMPTY_ARRAY = new Object[0];

    // built-in handlers
    private static final Map<Member, CopyOnWriteSortedSet<XC_MethodHook>> sHookedMethodCallbacks = new HashMap<>();
    /*package*/ public static final CopyOnWriteSortedSet<XC_LoadPackage> sLoadedPackageCallbacks = new CopyOnWriteSortedSet<>();
    /*package*/public static final CopyOnWriteSortedSet<XC_InitPackageResources> sInitPackageResourcesCallbacks = new CopyOnWriteSortedSet<>();

    private XposedBridge() {
    }

    /**
     * Returns the currently installed version of the Xposed framework.
     */
    public static int getXposedVersion() {
        return 89;
    }
    /**
     * Writes a message to the Xposed error log.
     *
     * <p class="warning"><b>DON'T FLOOD THE LOG!!!</b> This is only meant for error logging.
     * If you want to write information/debug messages, use logcat.
     *
     * @param text The log message.
     */
    public synchronized static void log(String text) {
        Log.i(TAG, text);
    }

    /**
     * Logs a stack trace to the Xposed error log.
     *
     * <p class="warning"><b>DON'T FLOOD THE LOG!!!</b> This is only meant for error logging.
     * If you want to write information/debug messages, use logcat.
     *
     * @param t The Throwable object for the stack trace.
     */
    public synchronized static void log(Throwable t) {
        Log.e(TAG, Log.getStackTraceString(t));
    }

    /**
     * Hook any method (or constructor) with the specified callback. See below for some wrappers
     * that make it easier to find a method/constructor in one step.
     *
     * @param hookMethod The method to be hooked.
     * @param callback The callback to be executed when the hooked method is called.
     * @return An object that can be used to remove the hook.
     * @see XposedHelpers#findAndHookMethod(String, ClassLoader, String, Object...)
     * @see XposedHelpers#findAndHookMethod(Class, String, Object...)
     * @see #hookAllMethods
     * @see XposedHelpers#findAndHookConstructor(String, ClassLoader, Object...)
     * @see XposedHelpers#findAndHookConstructor(Class, Object...)
     * @see #hookAllConstructors
     */
    public static XC_MethodHook.Unhook hookMethod(final Member hookMethod, final XC_MethodHook callback) {
        if (!(hookMethod instanceof Method) && !(hookMethod instanceof Constructor<?>)) {
            throw new IllegalArgumentException("Only methods and constructors can be hooked: " + hookMethod.toString());
        } else if (hookMethod.getDeclaringClass().isInterface()) {
            throw new IllegalArgumentException("Cannot hook interfaces: " + hookMethod.toString());
        } else if (Modifier.isAbstract(hookMethod.getModifiers())) {
            throw new IllegalArgumentException("Cannot hook abstract methods: " + hookMethod.toString());
        }
        XLog.d(" hookMethod  ");
        boolean newMethod = false;
        CopyOnWriteSortedSet<XC_MethodHook> callbacks;
        synchronized (sHookedMethodCallbacks) {
            callbacks = sHookedMethodCallbacks.get(hookMethod);
            if (callbacks == null) {
                callbacks = new CopyOnWriteSortedSet<>();
                sHookedMethodCallbacks.put(hookMethod, callbacks);
                newMethod = true;
            }
        }
        callbacks.add(callback);

        if (newMethod) {
            Class<?> declaringClass = hookMethod.getDeclaringClass();
            Class<?>[] parameterTypes;
            if (hookMethod instanceof Constructor<?>) {
                parameterTypes = ((Constructor<?>) hookMethod).getParameterTypes();
                Object[] types = getRealParameterTypes(hookMethod, callback, parameterTypes);
                DexposedHelper.findAndHookConstructor(declaringClass, types);
            } else {
                parameterTypes = ((Method) hookMethod).getParameterTypes();
                Object[] types = getRealParameterTypes(hookMethod, callback, parameterTypes);
                DexposedHelper.findAndHookMethod(declaringClass, hookMethod.getName(), types);
            }
        }

        return callback.new Unhook(hookMethod);
    }

    private static Object[] getRealParameterTypes(final Member hookMethod, final XC_MethodHook callback, Class<?>[] parameterTypes) {
        Object[] types = new Object[parameterTypes.length + 1];
        int i = 0;
        for (Class<?> clas : parameterTypes) {
            types[i] = clas;
            i++;
        }
        types[i] = new DXC_MethodHook() {
            @Override
            public void beforeHookedMethod(XMethodHookParams param) {
                log("beforeHookedMethod  " + skip);
                CopyOnWriteSortedSet<XC_MethodHook> xcMethodHook = sHookedMethodCallbacks.get(hookMethod);
                if (xcMethodHook == null || xcMethodHook.isEmpty() || xcMethodHook.indexOf(callback) == -1) {
                    skip = false;
                    return;
                }
                if (!skip) {
                    try {
                        MethodHookParam hookParam = new MethodHookParam(param);
                        callback.beforeHookedMethod(hookParam);
                        param.method = hookParam.method;
                        param.thisObject = hookParam.thisObject;
                        param.args = hookParam.args;
                        param.result = hookParam.getResult();
                        param.throwable = hookParam.getThrowable();
                        param.earlyReturn = hookParam.returnEarly;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } else if (param.result != null) {
                    param.setResult(param.result);
                }
                skip = false;
            }

            @Override
            public void afterHookedMethod(XMethodHookParams param) {
                CopyOnWriteSortedSet<XC_MethodHook> xcMethodHook = sHookedMethodCallbacks.get(hookMethod);
                if (xcMethodHook == null || xcMethodHook.isEmpty() || xcMethodHook.indexOf(callback) == -1) {
                    skip = false;
                    return;
                }
                if (!skip) {
                    try {
                        callback.afterHookedMethod(new MethodHookParam(param));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
                skip = false;
            }
        };
        return types;
    }

    /**
     * Removes the callback for a hooked method/constructor.
     *
     * @param hookMethod The method for which the callback should be removed.
     * @param callback The reference to the callback as specified in {@link #hookMethod}.
     * @deprecated Use {@link XC_MethodHook.Unhook#unhook} instead. An instance of the {@code Unhook}
     * class is returned when you hook the method.
     */
    @Deprecated
    public static void unhookMethod(Member hookMethod, XC_MethodHook callback) {
        CopyOnWriteSortedSet<XC_MethodHook> callbacks;
        synchronized (sHookedMethodCallbacks) {
            callbacks = sHookedMethodCallbacks.get(hookMethod);
            if (callbacks == null) {
                return;
            }
        }
        callbacks.remove(callback);
    }

    /**
     * Hooks all methods with a certain name that were declared in the specified class. Inherited
     * methods and constructors are not considered. For constructors, use
     * {@link #hookAllConstructors} instead.
     *
     * @param hookClass The class to check for declared methods.
     * @param methodName The name of the method(s) to hook.
     * @param callback The callback to be executed when the hooked methods are called.
     * @return A set containing one object for each found method which can be used to unhook it.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Set<XC_MethodHook.Unhook> hookAllMethods(Class<?> hookClass, String methodName, XC_MethodHook callback) {
        Set<XC_MethodHook.Unhook> unhooks = new HashSet<>();
        for (Member method : hookClass.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                unhooks.add(hookMethod(method, callback));
            }
        }
        return unhooks;
    }

    /**
     * Hook all constructors of the specified class.
     *
     * @param hookClass The class to check for constructors.
     * @param callback The callback to be executed when the hooked constructors are called.
     * @return A set containing one object for each found constructor which can be used to unhook it.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Set<XC_MethodHook.Unhook> hookAllConstructors(Class<?> hookClass, XC_MethodHook callback) {
        Set<XC_MethodHook.Unhook> unhooks = new HashSet<>();
        for (Member constructor : hookClass.getDeclaredConstructors()) {
            unhooks.add(hookMethod(constructor, callback));
        }
        return unhooks;
    }

    /**
     * Adds a callback to be executed when an app ("Android package") is loaded.
     *
     * <p class="note">You probably don't need to call this. Simply implement {@link IXposedHookLoadPackage}
     * in your module class and Xposed will take care of registering it as a callback.
     *
     * @param callback The callback to be executed.
     * @hide
     */
    public static void hookLoadPackage(XC_LoadPackage callback) {
        synchronized (sLoadedPackageCallbacks) {
            sLoadedPackageCallbacks.add(callback);
        }
    }

    /**
     * Adds a callback to be executed when the resources for an app are initialized.
     *
     * <p class="note">You probably don't need to call this. Simply implement {@link IXposedHookInitPackageResources}
     * in your module class and Xposed will take care of registering it as a callback.
     *
     * @param callback The callback to be executed.
     * @hide
     */
    public static void hookInitPackageResources(XC_InitPackageResources callback) {
        synchronized (sInitPackageResourcesCallbacks) {
            sInitPackageResourcesCallbacks.add(callback);
        }
    }

    private static boolean skip = false;

    private static Object invokeOriginalMethodNative(Member method, int methodId,
            Class<?>[] parameterTypes, Class<?> returnType, Object thisObject, Object[] args)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        GenedClassInfo genedClassInfo = DexposedBridge.getGenedClassInfo(method);
        if (genedClassInfo != null) {
            try {
                skip = true;
                Method med = genedClassInfo.getHookedMethodInfo().getMethod();
                med.setAccessible(true);
                return med.invoke(thisObject, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else {
            log("genedClassInfo is null");
        }
        return null;
    }

    /**
     * Basically the same as {@link Method#invoke}, but calls the original method
     * as it was before the interception by Xposed. Also, access permissions are not checked.
     *
     * <p class="caution">There are very few cases where this method is needed. A common mistake is
     * to replace a method and then invoke the original one based on dynamic conditions. This
     * creates overhead and skips further hooks by other modules. Instead, just hook (don't replace)
     * the method and call {@code param.setResult(null)} in {@link XC_MethodHook#beforeHookedMethod}
     * if the original method should be skipped.
     *
     * @param method The method to be called.
     * @param thisObject For non-static calls, the "this" pointer, otherwise {@code null}.
     * @param args Arguments for the method call as Object[] array.
     * @return The result returned from the invoked method.
     * @throws NullPointerException if {@code receiver == null} for a non-static method
     * @throws IllegalAccessException if this method is not accessible (see {@link AccessibleObject})
     * @throws IllegalArgumentException if the number of arguments doesn't match the number of parameters, the receiver
     * is incompatible with the declaring class, or an argument could not be unboxed
     * or converted by a widening conversion to the corresponding parameter type
     * @throws InvocationTargetException if an exception was thrown by the invoked method
     */
    public static Object invokeOriginalMethod(Member method, Object thisObject, Object[] args)
            throws NullPointerException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (args == null) {
            args = EMPTY_ARRAY;
        }

        Class<?>[] parameterTypes;
        Class<?> returnType;
        if (method instanceof Method) {
            parameterTypes = ((Method) method).getParameterTypes();
            returnType = ((Method) method).getReturnType();
        } else if (method instanceof Constructor) {
            parameterTypes = ((Constructor<?>) method).getParameterTypes();
            returnType = null;
        } else {
            throw new IllegalArgumentException("method must be of type Method or Constructor");
        }

        return invokeOriginalMethodNative(method, 0, parameterTypes, returnType, thisObject, args);
    }

    /**
     * @hide
     */
    public static final class CopyOnWriteSortedSet<E> {
        private transient volatile Object[] elements = EMPTY_ARRAY;

        @SuppressWarnings("UnusedReturnValue")
        public synchronized boolean add(E e) {
            int index = indexOf(e);
            if (index >= 0) {
                return false;
            }

            Object[] newElements = new Object[elements.length + 1];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            newElements[elements.length] = e;
            Arrays.sort(newElements);
            elements = newElements;
            return true;
        }

        @SuppressWarnings("UnusedReturnValue")
        public synchronized boolean remove(E e) {
            int index = indexOf(e);
            if (index == -1) {
                return false;
            }

            Object[] newElements = new Object[elements.length - 1];
            System.arraycopy(elements, 0, newElements, 0, index);
            System.arraycopy(elements, index + 1, newElements, index, elements.length - index - 1);
            elements = newElements;
            return true;
        }

        private boolean isEmpty() {
            return elements.length == 0;
        }

        private int indexOf(Object o) {
            for (int i = 0; i < elements.length; i++) {
                if (o.equals(elements[i])) {
                    return i;
                }
            }
            return -1;
        }

        public Object[] getSnapshot() {
            return elements;
        }
    }
}
