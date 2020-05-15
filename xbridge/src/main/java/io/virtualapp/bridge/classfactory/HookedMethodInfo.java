package io.virtualapp.bridge.classfactory;

import io.virtualapp.bridge.DXC_MethodHook;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class HookedMethodInfo {

    private Member member = null;
	private DXC_MethodHook callBack = null;

	private Method method = null;
	private Constructor<?> constructor = null;
	
	private boolean _isStatic = false;
	
	//include this pointer(always null),for convenience,length always equal paramNo
	private BasicType[] paramBasicTypes = null;
	private int paramNo = 0;
	private int paramRegNo = 0;
	
	private boolean _hasReturn = false;
	private BasicType retBasicType = null;
	
	private boolean _hasThrowable = false;
	
	public HookedMethodInfo(Member member, DXC_MethodHook callBack) {
        this.member = member;
		this.callBack = callBack;

		if( member instanceof Method) {
			this.method = (Method) member;
			this.constructor = null;
			_isStatic = Modifier.isStatic(method.getModifiers());

			Class<?>[] paramClasses = method.getParameterTypes();
			paramNo = paramClasses.length + (_isStatic ? 0 : 1 );
			
			paramBasicTypes = new BasicType[paramNo];

			if( _isStatic ) {
				for(int i = 0 ; i < paramNo ; i ++) {
					paramBasicTypes[i] = getBasicTypeIfIs(paramClasses[i]);
				}
			} else {
				paramBasicTypes[0] = null;
				for(int i = 1 ; i < paramNo ; i ++) {
					paramBasicTypes[i] = getBasicTypeIfIs(paramClasses[i - 1]);
				}
			}
		
			_hasReturn = ! method.getReturnType().equals(void.class);
			if( _hasReturn ) {
				retBasicType = getBasicTypeIfIs(method.getReturnType());
			} else {
				retBasicType = null;
			}
			
			_hasThrowable = method.getExceptionTypes().length != 0;
			
		} else if( member instanceof Constructor<?>) {
			this.constructor = (Constructor<?>)member;
			this.method = null;
			_isStatic = false;
			_hasReturn = false;
			retBasicType = null;
			
			Class<?>[] paramClasses = constructor.getParameterTypes();
			paramNo = paramClasses.length + 1;
			paramBasicTypes = new BasicType[paramNo];
			paramBasicTypes[0] = null;
			for(int i = 1 ; i < paramNo ; i ++) {
				paramBasicTypes[i] = getBasicTypeIfIs(paramClasses[i - 1]);
			}
			_hasThrowable = constructor.getExceptionTypes().length != 0;

		} else if (member.getDeclaringClass().isInterface()) {
			throw new IllegalArgumentException("Cannot hook interfaces: " + member.toString());
		} else if (Modifier.isAbstract(member.getModifiers())) {
			throw new IllegalArgumentException("Cannot hook abstract methods: " + member.toString());
		} else {
			throw new IllegalArgumentException("Only methods and constructors can be hooked: " + member.toString());
		}
		
		//calculate how many regs that parameters used
		for(BasicType basicType : paramBasicTypes) {
			if( basicType != null && basicType.isWide()) paramRegNo ++;
		}
		paramRegNo += paramNo;
	}
	
	private BasicType getBasicTypeIfIs(Class<?> clazz) {

		if( clazz.equals(boolean.class) ){
			return BasicType.BOOLEAN;
		} else if( clazz.equals(byte.class) ){
			return BasicType.BYTE;
		} else if ( clazz.equals(char.class)) {
			return BasicType.CHAR;
		} else if( clazz.equals(short.class)) {
			return BasicType.SHORT;
		} else if( clazz.equals(int.class)) {
			return BasicType.INT;
		} else if( clazz.equals(long.class)) {
			return BasicType.LONG;
		} else if( clazz.equals(float.class)) {
			return BasicType.FLOAT;
		} else if( clazz.equals(double.class)) {
			return BasicType.DOUBLE;
		} else {
			return null;
		}
	}

	public Member getMember(){
	    return member;
    }

	public DXC_MethodHook getCallBack(){
		return callBack;
	}

	public boolean isStatic() {
		return _isStatic;
	}

	public BasicType[] getParamBasicTypes() {
		return paramBasicTypes;
	}
	
	public int getParamNo() {
		return paramNo;
	}
	
	public boolean hasReturn() {
		return _hasReturn;
	}
	
	public BasicType getRetBasicType() {
		return retBasicType;
	}
	
	public Method getMethod() {
		return this.method;
	}
	
	public Constructor<?> getConstructor(){
		return this.constructor;
	}

	public boolean hasThrowable() {
		return _hasThrowable;
	}

	public int getParamRegNo() {
		return paramRegNo;
	}

	@Override
	public String toString() {
		return "HookedMethodInfo [member=" + member + ", callBack=" + callBack + ", method=" + method + ", constructor="
				+ constructor + ", _isStatic=" + _isStatic + ", paramBasicTypes=" + Arrays.toString(paramBasicTypes)
				+ ", paramNo=" + paramNo + ", paramRegNo=" + paramRegNo + ", _hasReturn=" + _hasReturn
				+ ", retBasicType=" + retBasicType + ", _hasThrowable=" + _hasThrowable + "]";
	}

}
