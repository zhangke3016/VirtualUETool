package io.virtualapp.bridge.classfactory;


import io.virtualapp.bridge.DXC_MethodHook;
import io.virtualapp.bridge.DXC_MethodHook.XMethodHookParams;
import io.virtualapp.bridge.XLog;

/**
 * Created by bmax on 2018/4/9.
 */

public class GenedClassHelper {

	public static final String desc = "Lio/virtualapp/bridge/classfactory/GenedClassHelper;";
	public static final String getMethodHookParamsMethodName = "getMethodHookParams";
	public static final String getXCallBackMethodName = "getCallBack";

	public static XMethodHookParams getMethodHookParams(Class<?> clazz) {


        try {
            return (XMethodHookParams) clazz.getField(GenedClassInfo.paramsFieldName).get(null);
        } catch (Exception e) {
            XLog.e("unexpect error",e);
        }
        return null;

    }

	public static DXC_MethodHook getCallBack(Class<?> clazz) {

		try {
			return (DXC_MethodHook) clazz.getField(GenedClassInfo.callBackFieldName).get(null);
		} catch (Exception e) {
			XLog.e("unexpect error",e);
		}

		return null;
	}

}
