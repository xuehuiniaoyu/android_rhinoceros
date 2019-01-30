package org.zoon.rhinoceros.interceptor.i;

import android.content.Context;

import org.hjson.JsonObject;
import org.zoon.rhinoceros.dimens.HDimens;

public interface HInterceptor<T> {
    T onInterceptor(Context context, HDimens dimens, JsonObject jsonObject);
}
