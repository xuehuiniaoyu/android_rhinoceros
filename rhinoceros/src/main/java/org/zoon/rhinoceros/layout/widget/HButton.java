package org.zoon.rhinoceros.layout.widget;

import android.content.Context;
import android.widget.Button;

import org.hjson.JsonValue;
import org.zoon.rhinoceros.layout.widget.base.TextWapper;

public class HButton extends TextWapper<Button> {
    public HButton(Context context, JsonValue value) {
        super(context, value);
    }

    @Override
    public void setTextSize(JsonValue value) {
        super.setTextSize(value);
    }

    @Override
    public void onMeasure(int parentWidth, int parentHeight) {
        super.onMeasure(parentWidth, parentHeight);
    }
}
