package io.virtual.magictool.network.okhttp.interceptor;


import android.text.TextUtils;
import android.util.Log;
import io.virtual.magictool.network.bean.NetworkRecord;
import io.virtual.magictool.network.core.DefaultResponseHandler;
import io.virtual.magictool.network.core.NetworkInterpreter;
import io.virtual.magictool.network.core.RequestBodyHelper;
import io.virtual.magictool.network.okhttp.ForwardingResponseBody;
import io.virtual.magictool.network.okhttp.InterceptorUtil;
import io.virtual.magictool.network.okhttp.NetworkManager;
import io.virtual.magictool.network.okhttp.OkHttpInspectorRequest;
import io.virtual.magictool.network.okhttp.OkHttpInspectorResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 抓包拦截器
 */
public class DoraemonInterceptor implements Interceptor {
    public static final String TAG = "DoraemonInterceptor";

    private final NetworkInterpreter mNetworkInterpreter = NetworkInterpreter.get();

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkManager.isActive()) {
            Request request = chain.request();
            return chain.proceed(request);
        }

        Request request = chain.request();
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            ResponseBody responseBody = ResponseBody.create(response.body().contentType(), "");
            return new Response.Builder()
                    .code(400)
                    .message(String.format("%s==>Exception:%s", chain.request().url().host(), e.getMessage()))
                    .body(responseBody)
                    .build();
        }

        String strContentType = response.header("Content-Type");
        //如果是图片则不进行拦截
        if (InterceptorUtil.isImg(strContentType)) {
            return response;
        }
        //白名单过滤
        if (!matchWhiteHost(request)) {
            return response;
        }
        Log.d(TAG, "intercept strContentType: " + strContentType);

        int requestId = mNetworkInterpreter.nextRequestId();

        RequestBodyHelper requestBodyHelper = new RequestBodyHelper();
        OkHttpInspectorRequest inspectorRequest =
                new OkHttpInspectorRequest(requestId, request, requestBodyHelper);
        NetworkRecord record = mNetworkInterpreter.createRecord(requestId, inspectorRequest);
        try {
            response.close();
            response = chain.proceed(request);
        } catch (IOException e) {
            mNetworkInterpreter.httpExchangeFailed(requestId, e.toString());
            throw e;
        }

        NetworkInterpreter.InspectorResponse inspectorResponse = new OkHttpInspectorResponse(
                requestId,
                request,
                response);
        mNetworkInterpreter.fetchResponseInfo(record, inspectorResponse);

        ResponseBody body = response.body();
        InputStream responseStream = null;
        MediaType contentType = null;
        if (body != null) {
            contentType = body.contentType();
            responseStream = body.byteStream();
        }

        responseStream = mNetworkInterpreter.interpretResponseStream(
                contentType != null ? contentType.toString() : null,
                responseStream,
                new DefaultResponseHandler(mNetworkInterpreter, requestId, record));
        if (responseStream != null) {
            response = response.newBuilder()
                    .body(new ForwardingResponseBody(body, responseStream))
                    .build();
        }

        return response;
    }

    /**
     * 是否命中白名单规则
     *
     * @return bool
     */
    private boolean matchWhiteHost(Request request) {
//        List<WhiteHostBean> whiteHostBeans = DokitConstant.WHITE_HOSTS;
//        if (whiteHostBeans.isEmpty()) {
//            return true;
//        }
//
//        for (WhiteHostBean whiteHostBean : whiteHostBeans) {
//            if (TextUtils.isEmpty(whiteHostBean.getHost())) {
//                continue;
//            }
//            String realHost = request.url().host();
//            //正则判断
//            if (whiteHostBean.getHost().equalsIgnoreCase(realHost)) {
//                return true;
//            }
//        }

        return true;
    }


}