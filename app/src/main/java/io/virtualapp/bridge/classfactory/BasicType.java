package io.virtualapp.bridge.classfactory;

public enum BasicType {

	BOOLEAN(boolean.class,"Z","Ljava/lang/Boolean;",false,"valueOf","booleanValue"),
	BYTE(byte.class,"B","Ljava/lang/Byte;",false,"valueOf","byteValue"),
	CHAR(char.class,"C","Ljava/lang/Character;",false,"valueOf","charValue"),
	SHORT(short.class,"S","Ljava/lang/Short;",false,"valueOf","shortValue"),
	INT(int.class,"I","Ljava/lang/Integer;",false,"valueOf","intValue"),
	LONG(long.class,"J","Ljava/lang/Long;",true,"valueOf","longValue"),
	FLOAT(float.class,"F","Ljava/lang/Float;",false,"valueOf","floatValue"),
	DOUBLE(double.class,"D","Ljava/lang/Double;",true,"valueOf","doubleValue");
	
	private BasicType(Class<?> clazz,String typeDesc,String typeWrapperDesc,boolean isWide,String wrapMethodName,String unwrapMethodName) {
		this.clazz = clazz;
		this.typeDesc = typeDesc;
		this.typeWrapperDesc = typeWrapperDesc;
		this._isWide = isWide;
		this.wrapMethodName = wrapMethodName;
		this.unwrapMethodName = unwrapMethodName;
	}
	private Class<?> clazz;
	private String typeDesc = null;
	private String typeWrapperDesc = null;
	private boolean _isWide = false;
	private String wrapMethodName = null;
	private String unwrapMethodName = null;
	
	public Class<?> getBasicClass(){
		return this.clazz;
	}
	public String getTypeDesc() {
		return typeDesc;
	}
	public String getWrapperDesc() {
		return typeWrapperDesc;
	}
	public boolean isWide() {
		return _isWide;
	}
	public String getWrapMethodName() {
		return wrapMethodName;
	}
	public String getUnwrapMethodName() {
		return unwrapMethodName;
	}
}
