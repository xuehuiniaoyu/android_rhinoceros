package org.zoon.rhinoceros.layout.widget;

import android.content.Context;
import android.widget.TextView;

import org.hjson.JsonValue;
import org.zoon.rhinoceros.layout.widget.base.TextWapper;

public class HTextView extends TextWapper<TextView> {
    public HTextView(Context context, JsonValue value) {
        super(context, value);
    }
}
