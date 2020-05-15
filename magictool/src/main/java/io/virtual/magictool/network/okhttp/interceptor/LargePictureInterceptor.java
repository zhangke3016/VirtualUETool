package io.virtual.magictool.network.okhttp.interceptor;


import android.text.TextUtils;
import io.virtual.magictool.network.okhttp.InterceptorUtil;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 大图拦截器
 */
public class LargePictureInterceptor implements Interceptor {
    public static final String TAG = "LargePictureInterceptor";

    public static boolean SWITCH_BIG_IMG = true;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (!SWITCH_BIG_IMG) {
            return response;
        }
        String contentType = response.header("Content-Type");

        if (InterceptorUtil.isImg(contentType)) {
            if (isLargeImgOpen()) {
                processResponse(response);
            }
        }
        return response;
    }

    private boolean isLargeImgOpen() {
        return SWITCH_BIG_IMG;
    }


    private void processResponse(Response response) {
        String field = response.header("Content-Length");
        if (!TextUtils.isEmpty(field)) {
//            LargePictureManager.getInstance().process(response.request().url().toString(), Integer.parseInt(field));
        }
    }
}