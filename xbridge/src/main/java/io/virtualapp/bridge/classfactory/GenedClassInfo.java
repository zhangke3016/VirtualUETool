package io.virtualapp.bridge.classfactory;

import io.virtualapp.bridge.DXC_MethodHook;
import io.virtualapp.bridge.XLog;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class GenedClassInfo {
	
    public static final String replaceMethodName = "replace";
    public static final String backupMethodName = "backup";
    public static final String paramsFieldName = "params";
    public static final String callBackFieldName = "callBack";

	
	private String classDesc = null;
	private String fullName = null;
	private String methodDesc = null;
	
	private HookedMethodInfo hookedMethodInfo = null;

	private ClassLoader classLoader = null;

	//init after load generated class
	private Class<?> genedClass = null;
	private Constructor<?> constructor = null;
	private Method replace = null;
	private Method backup = null;

	public GenedClassInfo(String classDesc, HookedMethodInfo hookedMethodInfo) {
		this.classDesc = classDesc;
		this.hookedMethodInfo = hookedMethodInfo;

		//method desc
		StringBuilder sb = new StringBuilder();
		if( hookedMethodInfo.hasReturn() ) {
			if( hookedMethodInfo.getRetBasicType() != null) {
				sb.append(hookedMethodInfo.getRetBasicType().getTypeDesc());
			} else {
				sb.append(FinalStr.objectDesc);
			}
		} else {
			sb.append(FinalStr.voidName);
		}

		BasicType[] basicTypes = hookedMethodInfo.getParamBasicTypes();
		for(int i = 0 ; i < hookedMethodInfo.getParamNo(); i ++) {
			if( basicTypes[i] != null) {
				sb.append(basicTypes[i].getTypeDesc());
			} else {
				sb.append(FinalStr.objectDesc);
			}
		}
		methodDesc = sb.toString();
		
		//full name
		fullName = classDesc.replace("/", ".").substring(1 , classDesc.length() - 1);
	}
	
	public boolean initGenedClass(ClassLoader loader) {
		this.classLoader = loader;
		Class<?>[] genMethodParamClasses = new Class<?>[hookedMethodInfo.getParamNo()];
		BasicType[] basicTypes = hookedMethodInfo.getParamBasicTypes();
		for(int i = 0 ; i < hookedMethodInfo.getParamNo() ; i ++) {
			genMethodParamClasses[i] = basicTypes[i] == null ? Object.class : basicTypes[i].getBasicClass();
		}
		
		try {
			this.genedClass = Class.forName(fullName, true, loader);

			//If error occured here,Make sure your ClassLoader is true!!
			constructor = genedClass.getConstructor(Member.class, DXC_MethodHook.class);

			constructor.newInstance(hookedMethodInfo.getMember(),hookedMethodInfo.getCallBack());

			backup = genedClass.getMethod(backupMethodName, genMethodParamClasses);
			replace = genedClass.getMethod(replaceMethodName, genMethodParamClasses);
		} catch (Exception e) {
			XLog.e("error occur when init generated class,ClassInfo:" + this.toString(),e);
			return false;
		}
        return true;
	}

	public String getClassDesc() {
		return classDesc;
	}

	public String getFullName() {
		return fullName;
	}

	public String getMethodDesc() {
		return methodDesc;
	}

	public HookedMethodInfo getHookedMethodInfo() {
		return hookedMethodInfo;
	}
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public Class<?> getGenedClazz() {
		return genedClass;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public Method getReplace() {
		return replace;
	}

	public Method getBackup() {
		return backup;
	}
	@Override
	public String toString() {
		return "GenedClassInfo [classDesc=" + classDesc + ", fullName=" + fullName + ", methodDesc=" + methodDesc
				+ ", hookedMethodInfo=" + hookedMethodInfo + ", classLoader=" + classLoader + ", genedClass="
				+ genedClass + ", constructor=" + constructor + ", replace=" + replace + ", backup=" + backup + "]";
	}


}
