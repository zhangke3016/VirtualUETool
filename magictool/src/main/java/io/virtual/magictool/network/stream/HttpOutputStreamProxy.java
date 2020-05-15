package io.virtual.magictool.network.stream;

import io.virtual.magictool.network.bean.NetworkRecord;
import io.virtual.magictool.network.core.NetworkInterpreter;
import io.virtual.magictool.network.core.RequestBodyHelper;
import io.virtual.magictool.network.okhttp.NetworkManager;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author: linjizong
 *  2019/3/14
 * @desc:
 */
public class HttpOutputStreamProxy extends OutputStreamProxy {
    private final int mRequestId;
    private final NetworkInterpreter mInterpreter;

    public HttpOutputStreamProxy(OutputStream out,int requestId, NetworkInterpreter interpreter) {
        super(out);
        mRequestId = requestId;
        mInterpreter = interpreter;
    }

    @Override
    protected  void onStreamComplete() throws IOException {
        NetworkRecord record = NetworkManager.get().getRecord(mRequestId);
        if (record != null && record.mRequest != null) {
            RequestBodyHelper requestBodyHelper = new RequestBodyHelper();
            try {
                OutputStream out = requestBodyHelper.createBodySink(record.mRequest.encode);
                mOutputStream.writeTo(out);
            } finally {
                out.close();
            }
            byte[] body = requestBodyHelper.getDisplayBody();
            mInterpreter.fetRequestBody(record, body);
        }
    }

}
