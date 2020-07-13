package me.ele.uetool.base;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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

    private MMKV getMMKV() {
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
        }
    }

    public Set<String> getStringSet(String key) {
        return myMMKV.decodeStringSet(key, null);
    }

    public boolean setElement(String key, List<ElementBean> value) {
        if (value == null) {
            myMMKV.removeValueForKey(key);
            return true;
        } else {
            Set<String> stringSet = new HashSet<>(value.size());
            for (ElementBean bean : value) {
                stringSet.add(JsonUtil.getInstance().toJsonString(bean));
            }
            return myMMKV.encode(key, stringSet);
        }
    }

    /**
     * 排好序的list
     *
     * @param key
     * @return
     */
    public List<ElementBean> getElements(String key) {
        try {
            Set<String> strings = getStringSet(key);
            if (strings != null && strings.size() > 0) {
                List<ElementBean> result = new ArrayList<ElementBean>(strings.size());
                for (String item : strings) {
                    result.add(JsonUtil.getInstance().parse(item, ElementBean.class));
                }
                Collections.sort(result, new Comparator<ElementBean>() {
                    @Override
                    public int compare(ElementBean o1, ElementBean o2) {
                        return o1.getSort() - o2.getSort();
                    }
                });
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
