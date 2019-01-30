package org.zoon.rhinoceros.layout.widget;

import android.content.Context;
import android.widget.ScrollView;

import org.hjson.JsonValue;
import org.zoon.rhinoceros.layout.widget.base.GroupWapper;

public class HScrollView extends GroupWapper<ScrollView> {
    public HScrollView(Context context, JsonValue value) {
        super(context, value);
    }
}
