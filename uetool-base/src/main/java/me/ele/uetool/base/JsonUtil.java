package me.ele.uetool.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonUtil {
	private static Gson mGson;

	private static JsonUtil instance;

	private static Gson getGson() {
		if (null == mGson) {
			mGson = new GsonBuilder()
					.setPrettyPrinting()
					.disableHtmlEscaping()
					.create();
		}
		return mGson;
	}

	public static JsonUtil getInstance() {
		if (null == instance) {
			instance = new JsonUtil();
		}
		return instance;
	}

	public <T> T parse(String response, Class<T> clz) {
		try {
			//解析中文
			return getGson().fromJson(new String(response.getBytes(), "UTF-8"), clz);
		} catch (Exception e) {
			return null;
		}
	}

	public <T> T parse(String response, Type type) {
		return getGson().fromJson(response, type);
	}

	public String toJsonString(Object clz) {
		return getGson().toJson(clz);
	}

	/**
	 * 将Json数组解析成相应的映射对象列表
	 * @param myClass
	 * @param jsonStr
	 * @param <T>
	 * @return
	 */
	public <T> ArrayList<T> parseJsonArray(String jsonStr, Class<T> myClass) {
		return getGson().fromJson(jsonStr, new ListParameterizedType(myClass));
	}

	private class ListParameterizedType implements ParameterizedType {
		private Type type;

		private ListParameterizedType(Type type) {
			this.type = type;
		}

		@Override
		public Type[] getActualTypeArguments() {
			return new Type[]{type};
		}

		@Override
		public Type getRawType() {
			return ArrayList.class;
		}

		@Override
		public Type getOwnerType() {
			return null;
		}
	}
}
