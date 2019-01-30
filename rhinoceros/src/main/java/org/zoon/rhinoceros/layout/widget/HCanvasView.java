package org.zoon.rhinoceros.layout.widget;

import android.content.Context;
import android.graphics.Canvas;

import org.hjson.JsonValue;
import org.zoon.rhinoceros.layout.tools.FunctionExec;
import org.zoon.rhinoceros.layout.widget.android_native.CanvasView;

public class HCanvasView extends HView<CanvasView> {
    public HCanvasView(Context context, JsonValue value) {
        super(context, value);
    }

    /**
     *
     * @param value
     */
    public void setOnDraw(JsonValue value) {
        mView.setListener(new CanvasView.Listener<JsonValue>(value) {
            @Override
            public void onDraw(JsonValue obj, Canvas canvas) {
                FunctionExec functionExec = new FunctionExec(obj, canvas);
                functionExec.exec(getContext(), getJsChannel(), getReflect());
            }
        });
    }

}
