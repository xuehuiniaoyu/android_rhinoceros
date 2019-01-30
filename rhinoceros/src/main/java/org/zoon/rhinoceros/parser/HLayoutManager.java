package org.zoon.rhinoceros.parser;

import android.content.Context;
import android.util.Log;

import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.zoon.rhinoceros.dimens.SimpleHDimens;
import org.zoon.rhinoceros.dimens.HDimens;
import org.zoon.rhinoceros.interceptor.i.HInterceptor;

public class HLayoutManager {
    public static final String TAG = HLayoutManager.class.getSimpleName();

    private Context context;
    public HLayoutManager(Context context) {
        this.context = context;
    }

    public Object[] layout(String template, HDimens dimens, HInterceptor... hInterceptors) {
        Log.i(TAG, "load template:"+template);
        Object[] results = new Object[hInterceptors.length];
        JsonObject root = JsonValue.readHjson(template).asObject();
        for(int i = 0; i < hInterceptors.length; i++) {
            HInterceptor hInterceptor = hInterceptors[i];
            results[i] = hInterceptor.onInterceptor(context, dimens, root);
        }
        return results;
    }

    public void layout(String template, HInterceptor... hInterceptors) {
        layout(template, new SimpleHDimens(), hInterceptors);
    }
}
