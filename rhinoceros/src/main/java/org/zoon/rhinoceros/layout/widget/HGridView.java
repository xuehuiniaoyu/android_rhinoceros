package org.zoon.rhinoceros.layout.widget;

import android.content.Context;
import android.widget.GridView;

import org.hjson.JsonValue;
import org.zoon.rhinoceros.annotations.Extension;
import org.zoon.rhinoceros.layout.tools.UiFuncExec;
import org.zoon.rhinoceros.layout.widget.base.AdapterWapper;

public class HGridView extends AdapterWapper<GridView> {
    public HGridView(Context context, JsonValue value) {
        super(context, value);
    }

    public void setColumns(JsonValue value) {
        mView.setNumColumns(value.asInt());
    }

    /**
     *
     * 设置行间距
     * verticalSpacing: 1
     * @param value
     */

    @Extension("vGap")
    public void setVerticalSpacing(JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec("setVerticalSpacing", value) {
            @Override
            protected void onExec(Object[] values) {
                mView.setVerticalSpacing((int) getDimens().getHeight((JsonValue) values[0]).getSize());
            }
        });
    }

    /**
     *
     * 设置列间距
     * horizontalSpacing: 1
     * @param value
     */
    @Extension("hGap")
    public void setHorizontalSpacing(JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec("setHorizontalSpacing", value) {
            @Override
            protected void onExec(Object[] values) {
                mView.setHorizontalSpacing((int) getDimens().getHeight((JsonValue) values[0]).getSize());
            }
        });
    }
}
