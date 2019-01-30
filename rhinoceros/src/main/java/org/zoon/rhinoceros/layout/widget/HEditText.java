package org.zoon.rhinoceros.layout.widget;

import android.content.Context;
import android.widget.EditText;

import org.hjson.JsonValue;
import org.zoon.rhinoceros.layout.widget.base.TextWapper;

public class HEditText extends TextWapper<EditText> {
    public HEditText(Context context, JsonValue value) {
        super(context, value);
    }
}
