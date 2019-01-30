package org.zoon.rhinoceros.layout.widget.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.hjson.JsonValue;
import org.zoon.rhinoceros.js.channel.JsChannel;
import org.zoon.rhinoceros.layout.widget.HView;
import org.zoon.rhinoceros.parser.HLayoutInflater;

import java.util.HashMap;

public class GroupWapper<T extends ViewGroup> extends HView<T> {
    public GroupWapper(Context context, JsonValue value) {
        super(context, value);
    }

    public void onMeasure(int parentWidth, int parentHeight) {
        super.onMeasure(parentWidth, parentHeight);
        if(getChildren() != null) {
            int len = getChildren().size();
            for(int i = 0; i < len; i++) {
                HView<? extends View> layoutHNode = (HView<? extends View>) getChildAt(i);
                layoutHNode.onMeasure(mViewLp.width > 0 ? mViewLp.width : parentWidth, mViewLp.height > 0 ? mViewLp.height : parentHeight);
            }
        }
    }

    @Override
    public void onLayout() {
        super.onLayout();
        if(getChildren() != null) {
            int len = getChildren().size();
            for(int i = 0; i < len; i++) {
                HView<? extends View> layoutHNode = (HView<? extends View>) getChildAt(i);
                mView.addView(layoutHNode.getView());
            }
        }
    }

    @Override
    public void setJsChannel(JsChannel jsChannel) {
        super.setJsChannel(jsChannel);
        if(getChildren() != null) {
            int len = getChildren().size();
            for(int i = 0; i < len; i++) {
                HView<? extends View> layoutHNode = (HView<? extends View>) getChildAt(i);
                layoutHNode.setJsChannel(jsChannel);
            }
        }
    }

    @Override
    public void setLayoutInflater(HLayoutInflater layoutInflater) {
        super.setLayoutInflater(layoutInflater);
        if(getChildren() != null) {
            int len = getChildren().size();
            for(int i = 0; i < len; i++) {
                HView<? extends View> layoutHNode = (HView<? extends View>) getChildAt(i);
                layoutHNode.setLayoutInflater(layoutInflater);
            }
        }
    }

    @Override
    public void onAdapterGetView(HashMap<String, Object> map) {
        super.onAdapterGetView(map);
        if(getChildren() != null) {
            int len = getChildren().size();
            for(int i = 0; i < len; i++) {
                HView<? extends View> layoutHNode = (HView<? extends View>) getChildAt(i);
                layoutHNode.onAdapterGetView(map);
            }
        }
    }
}
