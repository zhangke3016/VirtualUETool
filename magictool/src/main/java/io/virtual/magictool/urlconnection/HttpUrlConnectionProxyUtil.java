package io.virtual.magictool.urlconnection;

import android.util.Log;
import io.virtual.magictool.network.okhttp.interceptor.DoraemonInterceptor;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-16-14:54
 * 描    述：ams 动态插入代码
 * 修订历史：
 * ================================================
 */
public class HttpUrlConnectionProxyUtil {
    //private static final String TAG = "HttpUrlConnectionProxyUtil";
    private static String[] hosts = new String[]{"amap.com"};
    public static final long DEFAULT_MILLISECONDS = 60000;      //默认的超时时间

    public static URLConnection proxy(URL url) {
        try {
            String host = HttpUrl.parse(url.toString()).host();
            if (isIgnore(host)) {
                return url.openConnection();
            }
            return createOkHttpURLConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static URLConnection createOkHttpURLConnection(URL url) throws Exception {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        addInterceptor(builder);
        OkHttpClient mClient = builder
                .retryOnConnectionFailure(true)
                .addInterceptor(new DoraemonInterceptor())
                .readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .build();

//        String strUrl = urlConnection.getURL().toString();
//        URL url = new URL(strUrl);
        String protocol = url.getProtocol().toLowerCase();
        if (protocol.equalsIgnoreCase("http")) {
            Log.d("HttpUrlConnection", " protocol： " + protocol);
            return new ObsoleteUrlFactory.OkHttpURLConnection(url, mClient);
        }

        if (protocol.equalsIgnoreCase("https")) {
            Log.d("HttpUrlConnection", " protocol： " + protocol);
            return new ObsoleteUrlFactory.OkHttpsURLConnection(url, mClient);
        }

        return url.openConnection();

    }

    private static void addInterceptor(OkHttpClient.Builder builder) {
        // 判断当前是否已经添加了拦截器，如果已添加则返回
        for (Interceptor interceptor : builder.interceptors()) {
            if (interceptor instanceof DoraemonInterceptor) {
                return;
            }
        }

        builder
                //添加mock拦截器
//                .addInterceptor(new MockInterceptor())
                //添加大图检测拦截器
//                .addInterceptor(new LargePictureInterceptor())
                //添加dokit拦截器
                .addInterceptor(new DoraemonInterceptor());
                //添加弱网 拦截器
//                .addNetworkInterceptor(new DoraemonWeakNetworkInterceptor());
    }

    /**
     * 判断是否过滤指定的host
     *
     * @param host
     * @return
     */
    private static boolean isIgnore(String host) {
        for (String jumpHost : hosts) {
            if (host.contains(jumpHost)) {
                return true;
            }
        }
        return false;
    }
}
