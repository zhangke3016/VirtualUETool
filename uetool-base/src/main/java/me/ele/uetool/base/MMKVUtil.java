package me.ele.uetool.base;

import com.tencent.mmkv.MMKV;

import java.util.Set;

/**
 * @author zijian.cheng
 * @date 2020/7/9
 */
public class MMKVUtil {
    private static volatile MMKVUtil ourInstance;

    private MMKV myMMKV;

    public static MMKVUtil getInstance() {
        if (ourInstance == null) {
            synchronized (MMKVUtil.class) {
                if (ourInstance == null) {
                    ourInstance = new MMKVUtil();
                }
            }
        }
        return ourInstance;
    }

    private MMKVUtil() {
        if (myMMKV == null) {
            myMMKV = MMKV.mmkvWithID("zzmmkv", MMKV.MULTI_PROCESS_MODE, null);
        }
    }

    public MMKV getMMKV() {
        return myMMKV;
    }

    public void set(String key, Object value) {
        if (value instanceof Boolean) {
            myMMKV.encode(key, (boolean) value);
        } else if (value instanceof Integer) {
            myMMKV.encode(key, (int) value);
        } else if (value instanceof Long) {
            myMMKV.encode(key, (long) value);
        } else if (value instanceof Float) {
            myMMKV.encode(key, (float) value);
        } else if (value instanceof Double) {
            myMMKV.encode(key, (double) value);
        } else if (value instanceof String) {
            myMMKV.encode(key, (String) value);
        } else if (value instanceof byte[]) {
            myMMKV.encode(key, (byte[]) value);
        } else if (value instanceof Set) {
            myMMKV.encode(key, (Set) value);
        }
    }

    public <T> T get(String key, Object type) {
        if (type instanceof Boolean) {
            myMMKV.decodeBool(key);
        } else if (type instanceof Integer) {
            myMMKV.decodeInt(key);
        } else if (type instanceof Long) {
            myMMKV.decodeLong(key);
        } else if (type instanceof Float) {
            myMMKV.decodeFloat(key);
        } else if (type instanceof Double) {
            myMMKV.decodeDouble(key);
        } else if (type instanceof String) {
            myMMKV.decodeString(key);
        } else if (type instanceof byte[]) {
            myMMKV.decodeBytes(key);
        }
        return null;
    }

    public String[] allKeys() {
        return myMMKV.allKeys();
    }

    public long totalSize() {
        return myMMKV.totalSize();
    }

    public long count() {
        return myMMKV.count();
    }

    public boolean containsKey(String key) {
        return myMMKV.containsKey(key);
    }

    public void removeValueForKey(String key) {
        myMMKV.removeValueForKey(key);
    }

    public void removeValuesForKeys(String[] keys) {
        myMMKV.removeValuesForKeys(keys);
    }

    public void clearAll() {
        myMMKV.clearAll();
    }

    public void clearMemoryCache() {
        myMMKV.clearMemoryCache();
    }

    public String mmapID() {
        return myMMKV.mmapID();
    }

    public boolean isFileValid(String key) {
        return MMKV.isFileValid(key);
    }
}
