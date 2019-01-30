package org.zoon.rhinoceros.layout.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;

import org.hjson.JsonArray;
import org.hjson.JsonValue;
import org.zoon.rhinoceros.annotations.Extension;
import org.zoon.rhinoceros.dimens.HDimens;
import org.zoon.rhinoceros.exception.HException;
import org.zoon.rhinoceros.exception.HLayoutException;
import org.zoon.rhinoceros.js.channel.JsChannel;
import org.zoon.rhinoceros.layout.HNode;
import org.zoon.rhinoceros.layout.tools.FunctionExec;
import org.zoon.rhinoceros.layout.tools.UiFuncExec;
import org.zoon.rhinoceros.parser.HLayoutInflater;
import org.zoon.rhinoceros.utils.HUri;
import org.zoon.rhinoceros.utils.IDUtil;
import org.zoon.rhinoceros.utils.Reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import glide.GlideApp;
import glide.GlideRequest;

public class HView<T extends View> extends HNode {

    /**
     * Android Native View
     */
    protected final T mView;

    /**
     * View的尺寸管理工具LayoutParams
     */
    protected LayoutParams mViewLp;

    /**
     * 上下文
     */
    private Context context;

    /**
     * 反射工具，用于动态执行代码
     */
    private Reflect reflect;

    /**
     * 尺寸管理工具，用于计算尺寸
     */
    private HDimens dimens;

    /**
     * 布局解析器
     */
    private HLayoutInflater layoutInflater;


    /**
     * 延迟执行工具，等到UI被加载后再执行方法
     */
    protected final List<UiFuncExec> mUiFuncExecList = new ArrayList<UiFuncExec>() {
        @Override
        public boolean add(UiFuncExec o) {
            if (this.contains(o)) {
                this.remove(o);
            }
            return super.add(o);
        }
    };

    protected final List<UiFuncExec> mAfterReadyUiFuncExecList = new ArrayList<UiFuncExec>() {
        @Override
        public boolean add(UiFuncExec o) {
            if (this.contains(o)) {
                this.remove(o);
            }
            return super.add(o);
        }
    };


    /**
     * js 通道，用于与js脚本交互
     */
    private JsChannel jsChannel;

    /**
     * 设置js通道
     *
     * @param jsChannel
     */
    public void setJsChannel(JsChannel jsChannel) {
        this.jsChannel = jsChannel;
    }

    public JsChannel getJsChannel() {
        return jsChannel;
    }

    /**
     * 设置布局解析器
     *
     * @param layoutInflater
     */
    public void setLayoutInflater(HLayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public HLayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    public Context getContext() {
        return context;
    }

    public Reflect getReflect() {
        return reflect;
    }

    public HDimens getDimens() {
        return dimens;
    }

    public T getView() {
        return mView;
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {
        public LayoutParams(int w, int h) {
            super(w, h);
        }

        /**
         * 权重总和
         */
        public int widthWeightSum;
        public int heightWeightSum;

        public int widthWeight;
        public int heightWeight;
    }

    @SuppressLint("NewApi")
    public HView(Context context, JsonValue value) {
        super(value);
        this.context = context;
        reflect = new Reflect();
        ParameterizedType c = getGenericSuperclass(getClass());
        if (c != null) {
            Type[] types = c.getActualTypeArguments();
            if (types != null && types.length == 1) {
                try {
                    types[0].getTypeName();
                } catch (Throwable t) {
                }
                String mViewTypeClassName = reflect.clear().on(types[0]).get("name");
                mView = reflect.on(mViewTypeClassName, context.getClassLoader()).constructor(Context.class, AttributeSet.class).newInstance(context, null);
            } else {
                throw new HLayoutException("Do not specify the correct generic types to <T extends View>");
            }
        } else {
            throw new HLayoutException("Do not specify the correct generic types to <T extends View>");
        }
//        mViewLp = reflect.clear().on(mView.getClass().getName()+"$LayoutParams").constructor(int.class, int.class)
//                .newInstance(0, 0);
        mViewLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mView.setLayoutParams(mViewLp);
//        mView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (dimens.getParentWidth() <= 0 || dimens.getParentHeight() <= 0) {
//                    onMeasure(mViewLp.width > 0 ? mViewLp.width : mView.getWidth(), mViewLp.height > 0 ? mViewLp.height : mView.getHeight());
//                }
//            }
//        });
        mView.post(new Runnable() {
            @Override
            public void run() {
                onUiReady();
            }
        });
    }

    private ParameterizedType getGenericSuperclass(Class clz) {
        Type type = clz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            return pType;
        }
        if (clz.getSuperclass() != null) {
            return getGenericSuperclass(clz.getSuperclass());
        }
        return null;
    }

    /**
     * 通过名称获取资源id
     * drawable/hello
     * raw/name
     *
     * @param name
     * @return
     */
    protected int getResourceId(String name) {
        if(!name.contains("@")) {
            throw new HException(name+" is not a Resource!");
        }
        name = name.substring(1);
        String[] scopeAndName = name.split("/");
        if (scopeAndName.length == 2) {
            int drawable = getReflect().clear().on(getContext().getPackageName() + ".R$" + scopeAndName[0]).get(scopeAndName[1]);
            return drawable;
        }
        return -1;
    }

    /**
     * 指定尺寸工具
     *
     * @param dimens
     */
    public final void dimens(HDimens dimens) {
        this.dimens = dimens;
    }

    @Override
    public void setAttr(String name, JsonValue value) {
        String extensionName = getExtension(name);
        if (extensionName != null) {
            reflect.clear().on(this).method(extensionName, JsonValue.class).invoke(value);
        } else {
            reflect.clear().on(this).method("set" + name.substring(0, 1).toUpperCase() + name.substring(1), JsonValue.class).invoke(value);
        }
    }

    /**
     * 设置id
     * id: xxx
     *
     * @param value
     */
    public void setId(JsonValue value) {
        mView.setId(IDUtil.id("#" + value.asString()));
    }

    /**
     * 设置布局宽度
     * width:100 |
     * width:50%
     *
     * @param value
     */
    public void setWidth(final JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec("setWidth", value) {
            @Override
            protected void onExec(Object[] values) {
                if(mViewLp.widthWeight == 0) {
                    mViewLp.width = (int) dimens.getWidth((JsonValue) values[0]).getSize();
                }
            }
        });
    }

    /**
     * 设置布局高度
     * height:100 |
     * height:50%
     *
     * @param value
     */
    public void setHeight(JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec("setHeight", value) {
            @Override
            protected void onExec(Object[] values) {
                if(mViewLp.heightWeight == 0) {
                    mViewLp.height = (int) dimens.getHeight((JsonValue) values[0]).getSize();
                }
            }
        });
    }

    /**
     * 设置横向权重
     *
     * @param value
     */
    public void setWidthWeight(JsonValue value) {
        mViewLp.widthWeight = value.asInt();
        mUiFuncExecList.add(new UiFuncExec("setWidthWeight", value) {
            @Override
            protected void onExec(Object[] values) {
                if (getParent() != null) {
                    HView parent = getParent();
                    if (parent.mViewLp.widthWeightSum > 0) {
                        mViewLp.width = (int) ((mViewLp.widthWeight * 1.0F / parent.mViewLp.widthWeightSum) * dimens.getWidth() * dimens.getWidthScale());
                    }
                }
            }
        });
    }

    /**
     * 设置纵向权重
     *
     * @param value
     */
    public void setHeightWeight(JsonValue value) {
        mViewLp.heightWeight = value.asInt();
        mUiFuncExecList.add(new UiFuncExec("setHeightWeight", value) {
            @Override
            protected void onExec(Object[] values) {
                if (getParent() != null) {
                    HView parent = getParent();
                    if (parent.mViewLp.heightWeightSum > 0) {
                        mViewLp.height = (int) ((mViewLp.heightWeight * 1.0F / parent.mViewLp.heightWeightSum) * dimens.getHeight() * dimens.getHeightScale());
                    }
                }
            }
        });
    }


    private CustomViewTarget<View, Bitmap> viewBgTarget;
    GlideRequest bgRequest;

    /**
     * 设置背景色
     * backgroundColor: "#cc0000"
     *
     * @param value
     */
    public void setBackgroundColor(JsonValue value) {
        mView.setBackgroundColor(parseColor(value));
    }

    /**
     *
     * 解析颜色
     * @param value
     * @return
     */
    protected int parseColor(JsonValue value) {
        String valueString = value.asString();
        if (valueString.contains("#")) {
            return Color.parseColor(value.asString());
        }
        else if(valueString.contains("@color/")) {
            int colorId = getResourceId(valueString);
            if(colorId == -1) {
                throw new HException(valueString + " is not Color!");
            }
            return context.getResources().getColor(colorId);
        }
        return Color.TRANSPARENT;
    }

    /**
     * 设置背景图或颜色
     *
     * @param value
     */
    public void setBackground(final JsonValue value) {
        String valueString = value.asString();
        if(HUri.isUri(valueString)) {
            viewBgTarget = new CustomViewTarget<View, Bitmap>(mView) {
                @Override
                protected void onResourceCleared(@Nullable Drawable placeholder) {

                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {

                }

                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    if (!isDied()) {
                        final Drawable drawable = new BitmapDrawable(resource);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mView.setBackground(drawable); //设置背景
                        }
                    }
                }
            };
            bgRequest = GlideApp.with(context).asBitmap().load(valueString).apply(new RequestOptions().centerInside());
            mAfterReadyUiFuncExecList.add(new UiFuncExec("setBackground") {
                @Override
                protected void onExec(Object[] values) {
                    bgRequest.into(viewBgTarget);
                }
            });
        }
        else if(valueString.contains("#") || valueString.contains("@color/")) {
            setBackgroundColor(value);
        }
        else {
            int resourceId = getResourceId(valueString);
            mView.setBackgroundResource(resourceId);
        }
    }

    /**
     * 设置外边距
     * margin: [0, 0, 0, 0]
     *
     * @param value
     */
    public void setMargin(JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec("setMargin", value.asArray()) {
            @Override
            protected void onExec(Object[] values) {
                JsonArray array = (JsonArray) values[0];
                mViewLp.setMargins((int) dimens.getWidth(array.get(0)).getSize(), (int) dimens.getHeight(array.get(1)).getSize(), (int) dimens.getWidth(array.get(2)).getSize(), (int) dimens.getHeight(array.get(3)).getSize());
            }
        });
    }

    /**
     * 设置内边距
     * padding: [0, 0, 0, 0]
     *
     * @param value
     */
    public void setPadding(JsonValue value) {
        mUiFuncExecList.add(new UiFuncExec("setPadding", value.asArray()) {
            @Override
            protected void onExec(Object[] values) {
                JsonArray array = (JsonArray) values[0];
                mView.setPadding((int) dimens.getWidth(array.get(0)).getSize(), (int) dimens.getHeight(array.get(1)).getSize(), (int) dimens.getWidth(array.get(2)).getSize(), (int) dimens.getHeight(array.get(3)).getSize());
            }
        });
    }

    /**
     * 设置在xxx左边
     * toLeftOf: id
     *
     * @param value
     */
    @Extension("leftOf")
    public void setToLeftOf(JsonValue value) {
        mViewLp.addRule(RelativeLayout.LEFT_OF, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx右边
     * toRightOf: id
     *
     * @param value
     */
    @Extension("rightOf")
    public void setToRightOf(JsonValue value) {
        mViewLp.addRule(RelativeLayout.RIGHT_OF, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx上边
     * toTopOf: id
     *
     * @param value
     */
    @Extension({"topOf", "above"})
    public void setToTopOf(JsonValue value) {
        mViewLp.addRule(RelativeLayout.ABOVE, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx下边
     * toBottomOf: id
     *
     * @param value
     */
    @Extension({"bottomOf", "below"})
    public void setToBottomOf(JsonValue value) {
        mViewLp.addRule(RelativeLayout.BELOW, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx左对齐
     * asLeft: id
     *
     * @param value
     */
    public void setAsLeft(JsonValue value) {
        mViewLp.addRule(RelativeLayout.ALIGN_LEFT, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx右对齐
     * asRight: id
     *
     * @param value
     */
    public void setAsRight(JsonValue value) {
        mViewLp.addRule(RelativeLayout.ALIGN_RIGHT, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx上对齐
     * asTop: id
     *
     * @param value
     */
    public void setAsTop(JsonValue value) {
        mViewLp.addRule(RelativeLayout.ALIGN_TOP, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx下对齐
     * asBottom: id
     *
     * @param value
     */
    public void setAsBottom(JsonValue value) {
        mViewLp.addRule(RelativeLayout.ALIGN_BOTTOM, IDUtil.id(value.asString()));
    }

    /**
     * 设置在xxx父容器左对齐
     * asParentLeft:  true | false
     *
     * @param value
     */
    public void setAsParentLeft(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置在xxx父容器右对齐
     * asParentRight:  true | false
     *
     * @param value
     */
    public void setAsParentRight(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置在xxx父容器上对齐
     * asParentTop:  true | false
     *
     * @param value
     */
    public void setAsParentTop(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置在xxx父容器下对齐
     * asParentBottom:  true | false
     *
     * @param value
     */
    public void setAsParentBottom(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置在xxx父容器居中
     * centerInParent:  true | false
     *
     * @param value
     */
    public void setCenterInParent(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置在父容器纵向居中
     * centerVertical: true | false
     *
     * @param value
     */
    @Extension({"center_v", "centerV"})
    public void setCenterVertical(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置在父容器纵向居中
     * centerHorizontal: true | false
     *
     * @param value
     */
    @Extension({"center_h", "centerH"})
    public void setCenterHorizontal(JsonValue value) {
        if (value.asBoolean()) {
            mViewLp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        }
    }

    /**
     * 设置相对父容器
     * asParent: left
     *
     * @param value
     */
    public void setAsParent(JsonValue value) {
        String valueString = value.asString();
        for(String v : valueString.split("\\|")) {
            switch (v) {
                case "left":
                    mViewLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    break;
                case "right":
                    mViewLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    break;
                case "top":
                    mViewLp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                    break;
                case "bottom":
                    mViewLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    break;
            }
        }
    }

    /**
     * 设置权重总和
     *
     * @param value
     */
    @Extension("widthSum")
    public void setWidthWeightSum(JsonValue value) {
        mViewLp.widthWeightSum = value.asInt();
    }

    /**
     * 设置权重占比
     *
     * @param value
     */
    @Extension("heightSum")
    public void setHeightWeightSum(JsonValue value) {
        mViewLp.heightWeightSum = value.asInt();
    }

    /**
     * 点击事件
     * onClick: add()
     *
     * @param value
     */
    public void setOnClick(final JsonValue value) {
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionExec functionExec = new FunctionExec(value, v);
                functionExec.exec(context, jsChannel, reflect);
            }
        });
    }

    /**
     * 长按事件
     * onLongClick: add()
     *
     * @param value
     */
    public void setOnLongClick(final JsonValue value) {
        mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                FunctionExec functionExec = new FunctionExec(value, v);
                functionExec.exec(context, jsChannel, reflect);
                return true;
            }
        });
    }

    /**
     * 焦点事件
     *
     * @param value
     */
    public void setOnFocus(final JsonValue value) {
        mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                FunctionExec functionExec = new FunctionExec(value, v, hasFocus);
                functionExec.exec(context, jsChannel, reflect);
            }
        });
    }

    /**
     * 设置按键事件
     *
     * @param value
     */
    public void setOnKey(final JsonValue value) {
        mView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                FunctionExec functionExec = new FunctionExec(value, v, keyCode, event);
                return (boolean) functionExec.exec(context, jsChannel, reflect);
            }
        });
    }

    /**
     * 触摸事件
     *
     * @param value
     */
    public void setOnTouch(final JsonValue value) {
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FunctionExec functionExec = new FunctionExec(value, v, event);
                return (boolean) functionExec.exec(context, jsChannel, reflect);
            }
        });
    }

    public void onMeasure(int parentWidth, int parentHeight) {
        Log.i("HView onMeasure", mView.getClass().getSimpleName() + " onMeasure:" + parentWidth + ", " + parentHeight);
        dimens.set(parentWidth, parentHeight).ok();
        for (UiFuncExec uiFuncExec : mUiFuncExecList) {
            uiFuncExec.exec();
        }
        requestLayout();
    }

    /**
     * 请求布局
     */
    public void requestLayout() {
        mView.requestLayout();
    }

    /**
     * 将内容转换成布局添加到window容器中显示
     */
    public void onLayout() {
    }

    /**
     * 当View被添加到Ui线程中之后执行
     */
    protected final void onUiReady() {
        for (UiFuncExec uiFuncExec : mAfterReadyUiFuncExecList) {
            uiFuncExec.exec();
        }
    }

    public void onAdapterGetView(HashMap<String, Object> map) {

    }
}