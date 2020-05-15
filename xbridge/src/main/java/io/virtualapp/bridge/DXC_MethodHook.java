package io.virtualapp.bridge;

import io.virtualapp.bridge.classfactory.GenedClassInfo;
import java.lang.reflect.Member;


/**
 * Created by bmax on 2018/4/8.
 */

public abstract class DXC_MethodHook {

    public static final String desc = "Lio/virtualapp/bridge/DXC_MethodHook;";
    public static final String beforeCallMethodName = "beforeHookedMethod";
    public static final String afterCallMethodName = "afterHookedMethod";

    public GenedClassInfo genedClassInfo;

    public void beforeHookedMethod(XMethodHookParams params) {


    }

    public void afterHookedMethod(XMethodHookParams params) {

    }

    //public void replaceCall(XMethodHookParams params){
        // setResult()
    //}
    
    public static class XMethodHookParams {

        public static final String desc = "Lio/virtualapp/bridge/DXC_MethodHook$XMethodHookParams;";

        public static final String methodFieldName = "method";
        public static final String thisObjectFieldName = "thisObject";
    	public static final String argsFieldName = "args";
    	public static final String resultFieldName = "result";
    	public static final String throwableFieldName = "throwable";
        public static final String isEarlyReturnMethodName = "isEarlyReturn";

        public Member method = null;
        public Object thisObject = null;
        public Object[] args = null;
        public Object result = null;
        public Throwable throwable = null;
        public boolean earlyReturn = false;

        public void setResult(Object result) {
            this.result = result;
            this.earlyReturn = true;
        }
        public boolean isEarlyReturn() {
            return this.earlyReturn;
        }
    }
}
