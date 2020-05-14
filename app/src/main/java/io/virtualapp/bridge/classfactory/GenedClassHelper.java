package io.virtualapp.bridge.classfactory;


import io.virtualapp.bridge.XLog;
import io.virtualapp.bridge.XC_MethodHook;

/**
 * Created by bmax on 2018/4/9.
 */

public class GenedClassHelper {

	public static final String desc = "Lio/virtualapp/bridge/classfactory/GenedClassHelper;";
	public static final String getMethodHookParamsMethodName = "getMethodHookParams";
	public static final String getBudCallBackMethodName = "getCallBack";

	public static XC_MethodHook.MethodHookParams getMethodHookParams(Class<?> clazz) {


        try {
            return (XC_MethodHook.MethodHookParams) clazz.getField(GenedClassInfo.paramsFieldName).get(null);
        } catch (Exception e) {
            XLog.e("unexpect error",e);
        }
        return null;

    }

	public static XC_MethodHook getCallBack(Class<?> clazz) {

		try {
			return (XC_MethodHook) clazz.getField(GenedClassInfo.callBackFieldName).get(null);
		} catch (Exception e) {
			XLog.e("unexpect error",e);
		}

		return null;
	}

}
