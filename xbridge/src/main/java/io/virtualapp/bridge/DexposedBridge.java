package io.virtualapp.bridge;

import android.content.Context;
import dalvik.system.DexClassLoader;
import io.virtualapp.bridge.classfactory.GenClasses;
import io.virtualapp.bridge.classfactory.GenedClassInfo;
import io.virtualapp.bridge.classfactory.HookedMethodInfo;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lab.galaxy.yahfa.HookMain;

public final class DexposedBridge {

    private static HashMap<Member, GenedClassInfo> hookedInfo = new HashMap<>();
    private static HashMap<Member, DXC_MethodHook> prepareHookCache = new HashMap<>();

    private static final String prefix = "Lio/virtualapp/bridge/GenedClass_";
    private static int suffix = 0;

    private static Context context = null;

    public static void init(Context context) {
        DexposedBridge.context = context;
    }

    public static void addPrepareHook(Member member, DXC_MethodHook XCMethodHook) {
        prepareHookCache.put(member, XCMethodHook);
    }

    public static void hookAllPrepared() {
        hookManyMethod(prepareHookCache);
        prepareHookCache.clear();
    }

    public static synchronized void hookManyMethod(HashMap<Member, DXC_MethodHook> membersAndCallBacks) {

        ArrayList<GenedClassInfo> genedClassInfos = new ArrayList<>();
        for (Map.Entry<Member, DXC_MethodHook> entry : membersAndCallBacks.entrySet()) {
            if (hookedInfo.containsKey(entry.getKey())) {
                XLog.v("already hook method:" + entry.getKey().toString());
                continue;
            }
            if (!checkMember(entry.getKey())) {
                continue;
            }
            HookedMethodInfo hookedMethodInfo = new HookedMethodInfo(entry.getKey(), entry.getValue());
            GenedClassInfo genedClassInfo = new GenedClassInfo(prefix + suffix++ + ";", hookedMethodInfo);
            genedClassInfos.add(genedClassInfo);
        }

        byte[] bytes = GenClasses.genManyClassesDexBytes(genedClassInfos);
        String dexFileName = "XHook" + suffix + ".dex";
        String dexPath = "/data/data/" + context.getPackageName() + "/files/" + dexFileName;

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(dexFileName, Context.MODE_PRIVATE);
            fos.write(bytes);
        } catch (Exception e) {
            XLog.e("error occur when write dex");
            return;
        }

        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, context.getCodeCacheDir().getAbsolutePath(), null, context.getClassLoader());
        for (GenedClassInfo genedClassInfo : genedClassInfos) {
            if (!genedClassInfo.initGenedClass(dexClassLoader)) {
                XLog.e("can not load or init generated class:" + genedClassInfo.toString());
                continue;
            }

            Member m = genedClassInfo.getHookedMethodInfo().getMember();
            Method replace = genedClassInfo.getReplace();
            Method backup = genedClassInfo.getBackup();

            HookMain.backupAndHook(m, replace, backup);

            hookedInfo.put(m, genedClassInfo);
        }

    }

    public static GenedClassInfo getGenedClassInfo(Member hookMethod) {
       return hookedInfo.get(hookMethod);
    }

    public static synchronized void hookMethod(Member hookMethod, DXC_MethodHook callBack) {

        if (!checkMember(hookMethod)) {
            return;
        }

        if (hookedInfo.containsKey(hookMethod)) {
            XLog.v("already hook method:" + hookMethod.toString());
            return;
        }

        HookedMethodInfo hookedMethodInfo = new HookedMethodInfo(hookMethod, callBack);
        GenedClassInfo genedClassInfo = new GenedClassInfo(prefix + suffix++ + ";", hookedMethodInfo);

        byte[] dexbytes = GenClasses.genOneClassDexBytes(genedClassInfo);
        String dexFileName = "XHook" + suffix + ".dex";
        String dexPath = "/data/data/" + context.getPackageName() + "/files/" + dexFileName;

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(dexFileName, Context.MODE_PRIVATE);
            fos.write(dexbytes);
        } catch (Exception e) {
            XLog.e("error occur when write dex");
            return;
        }

        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, context.getCodeCacheDir().getAbsolutePath(), null, callBack.getClass().getClassLoader());
        if (!genedClassInfo.initGenedClass(dexClassLoader)) {
            XLog.e("error occured while load class:" + genedClassInfo.toString());
            return;
        }

        Method replace = genedClassInfo.getReplace();
        Method backup = genedClassInfo.getBackup();

        HookMain.backupAndHook(hookMethod, replace, backup);

        hookedInfo.put(hookMethod, genedClassInfo);
    }

    private static boolean checkMember(Member member) {

        if (member instanceof Method) {
            return true;
        } else if (member instanceof Constructor<?>) {
            return true;
        } else if (member.getDeclaringClass().isInterface()) {
            XLog.e("Cannot hook interfaces: " + member.toString());
            return false;
        } else if (Modifier.isAbstract(member.getModifiers())) {
            XLog.e("Cannot hook abstract methods: " + member.toString());
            return false;
        } else {
            XLog.e("Only methods and constructors can be hooked: " + member.toString());
            return false;
        }
    }

}


