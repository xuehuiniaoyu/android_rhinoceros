package org.zoon.rhinoceros.parser;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;

import org.zoon.rhinoceros.dimens.HDimens;
import org.zoon.rhinoceros.dimens.PercentDimens;
import org.zoon.rhinoceros.exception.HLayoutException;
import org.zoon.rhinoceros.interceptor.BodyInterceptor;
import org.zoon.rhinoceros.interceptor.ScriptInterceptor;
import org.zoon.rhinoceros.js.channel.SilentlyJsChannel;
import org.zoon.rhinoceros.js.native_object.code.JavaPackage;
import org.zoon.rhinoceros.js.native_object.color.ColorManager;
import org.zoon.rhinoceros.js.native_object.console.Console;
import org.zoon.rhinoceros.js.native_object.net.Net;
import org.zoon.rhinoceros.js.native_object.reflect.RobustReflect;
import org.zoon.rhinoceros.js.native_object.util.Utils;
import org.zoon.rhinoceros.js.native_object.window.Document;
import org.zoon.rhinoceros.layout.widget.HView;
import org.zoon.rhinoceros.template.HTemplate;
import org.zoon.rhinoceros.template.SimpleHTemplate;
import org.zoon.rhinoceros.utils.IDUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class HLayoutInflater {

    public interface LayoutInflaterListener {
        void onFailed(Exception e, HLayoutInflater layoutInflater);
        void onSuccess(HView<? extends View> hView, HLayoutInflater layoutInflater);
    }

    public static final class InflaterType<T> {
        T type;
        HView<? extends View> hView;
        String layout;
        Exception err;
        LayoutInflaterListener layoutInflaterListener;

        public InflaterType(T type) {
            this.type = type;
        }
    }

    public void inflaterAsync(InflaterType<?> inflaterType, LayoutInflaterListener layoutInflaterListener) {
        inflaterType.layoutInflaterListener = layoutInflaterListener;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            final AsyncTask<InflaterType<?>, Void, InflaterType<?>> asyncTask
                    = new AsyncTask<InflaterType<?>, Void, InflaterType<?>>() {
                @Override
                protected InflaterType<?> doInBackground(InflaterType<?>... inflaterTypes) {
                    InflaterType<?> inflaterType = inflaterTypes[0];
                    if(inflaterType.type instanceof File) {
                        try {
                            inflaterType.layout = hTemplate.apply(new FileInputStream((File)inflaterType.type));
                        } catch (IOException e) {
                            inflaterType.err = e;
                        }
                    }
                    else if(inflaterType.type instanceof String) {
                        try {
                            inflaterType.layout = hTemplate.apply((String) inflaterType.type);
                        } catch (IOException e) {
                            inflaterType.err = e;
                        }
                    }
                    else if(inflaterType.type instanceof InputStream) {
                        try {
                            inflaterType.layout = hTemplate.apply((InputStream) inflaterType.type);
                        } catch (IOException e) {
                            inflaterType.err = e;
                        }
                    }
                    inflaterType.hView = inflater(inflaterType.layout);
                    return inflaterType;
                }

                @Override
                protected void onPostExecute(InflaterType<?> inflaterType) {
                    if(inflaterType.err != null) {
                        inflaterType.layoutInflaterListener.onFailed(inflaterType.err, HLayoutInflater.this);
                    }
                    else {
                        inflaterType.layoutInflaterListener.onSuccess(inflaterType.hView, HLayoutInflater.this);
                    }
                }
            };
            asyncTask.execute(inflaterType);
        }
    }


    public static View findViewById(Activity activity, String id) {
        return activity.findViewById(IDUtil.id(id));
    }

    public static View findViewById(View parent, String id) {
        return parent.findViewById(IDUtil.id(id));
    }

    /**
     *
     * 抽象的父容器宽度
     * 不管屏幕宽度是多少，你已设定值为准
     */
    private int scaleWidth;
    /**
     *
     * 抽象的父容器高度
     * 不管屏幕高度是多少，你已设定值为准
     */
    private int scaleHeight;

    private Context context;
    private HTemplate hTemplate;

    private HDimens dimens;


    /**
     * 布局解析器
     */
    private BodyInterceptor mBodyInterceptor = new BodyInterceptor();

    /**
     *
     *
     * 异步解析回调接口
     */
    private LayoutInflaterListener onLayoutInflaterListener;

    public HLayoutInflater(HLayoutInflater hLayoutInflater) {
        this.context = hLayoutInflater.context;
        setTemplate(hLayoutInflater.hTemplate);
        this.hTemplate.as("context", context);
        this.scaleWidth = hLayoutInflater.scaleWidth;
        this.scaleHeight = hLayoutInflater.scaleHeight;
        this.mJavaScriptInterface.putAll(hLayoutInflater.mJavaScriptInterface);
        this.mBodyInterceptor = hLayoutInflater.mBodyInterceptor;
//        System.arraycopy(hLayoutInflater.hInterceptors, 0, this.hInterceptors, 0, hInterceptors.length);
    }

    public HLayoutInflater setTemplate(HTemplate hTemplate) {
        this.hTemplate = new HTemplate(hTemplate);
        this.hTemplate.as("package", context.getPackageName());
        this.hTemplate.as("assets", "file:///android_asset");
        this.hTemplate.as("raw", "android.resource://"+context.getPackageName()+"/raw");
        this.hTemplate.as("drawable", "android.resource://"+context.getPackageName()+"/drawable");
        this.hTemplate.as("sdcard", "file://"+Environment.getExternalStorageDirectory().getPath());
        return this;
    }

    /**
     *
     * 替换
     * @param name
     * @param template
     * @return
     */
    public HLayoutInflater apply(String name, String template) {
        try {
            hTemplate.as(name, hTemplate.apply(template));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     *
     *
     * java对象映射到javascript
     */
    private HashMap<String, Object> mJavaScriptInterface = new HashMap<>();

    public HLayoutInflater(Context context) {
        this(context, new SimpleHTemplate(), 100, 100);
    }

    public HLayoutInflater(Context context, int scaleWidth, int scaleHeight) {
        this(context, new SimpleHTemplate(), scaleWidth, scaleHeight);
    }

    public HLayoutInflater(Context context, HTemplate hTemplate) {
        this(context, hTemplate, 100, 100);
    }

    public HLayoutInflater(Context context, HTemplate hTemplate, int scaleWidth, int scaleHeight) {
        this.context = context;
        setTemplate(hTemplate);
        this.hTemplate.as("context", context);
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;
    }

    public HLayoutInflater addJavaScriptInterface(String name, Object object) {
        mJavaScriptInterface.put(name, object);
        return this;
    }

    public HLayoutInflater setDimens(HDimens dimens) {
        this.dimens = dimens;
        return this;
    }

//    private HInterceptor[] hInterceptors = new HInterceptor[] {
//
//            new BodyInterceptor(),
//            new ScriptInterceptor(),
//
//    };

    public HView<? extends View> inflater(String layout) {
        //layout = layout.replace("&quot;", "\"");
        HLayoutManager groupManager = new HLayoutManager(context);
        Object[] results = groupManager.layout(layout, dimens != null ? dimens : new PercentDimens().scaleTo(scaleWidth, scaleHeight), mBodyInterceptor,  new ScriptInterceptor());
        HView<? extends View> mLayout = (HView<? extends View>) results[0];
        mLayout.setLayoutInflater(new HLayoutInflater(this));
        if(results[1] != null) {
            // 添加框架定义的接口对象
            SilentlyJsChannel mSilentlyJsChannel = new SilentlyJsChannel();
            mSilentlyJsChannel.addJavaScriptInterface("__context", context);
            mSilentlyJsChannel.addJavaScriptInterface("__reflect", new RobustReflect());
            mSilentlyJsChannel.addJavaScriptInterface("__console", new Console(context));
            mSilentlyJsChannel.addJavaScriptInterface("__document", new Document(context));
            mSilentlyJsChannel.addJavaScriptInterface("__net", new Net(context));
            mSilentlyJsChannel.addJavaScriptInterface("__package", new JavaPackage());
            mSilentlyJsChannel.addJavaScriptInterface("__utils", new Utils(context));
            mSilentlyJsChannel.addJavaScriptInterface("__color", new ColorManager(context));

            // 添加自定义接口对象
            for (String name : mJavaScriptInterface.keySet()) {
                mSilentlyJsChannel.addJavaScriptInterface(name, mJavaScriptInterface.get(name));
            }

            //mSilentlyJsChannel.loadJs(results[1].toString());
            ((ScriptInterceptor)results[1]).setJsChannel(mSilentlyJsChannel);
            mLayout.setJsChannel(mSilentlyJsChannel);
        }
        return mLayout;
    }

    /**
     * 解析布局
     * @param inputStream
     * @return
     * @throws IOException
     */
    public HView<? extends View> parse(InputStream inputStream) throws IOException {
        String template = hTemplate.apply(inputStream);
        return inflater(template);
    }

    /**
     * 解析布局 从文本内容
     * @param source
     * @return
     * @throws IOException
     */
    public HView<? extends View> parse(String source) throws IOException {
        String template = hTemplate.apply(source);
        return inflater(template);
    }

    /**
     * 解析布局 从文件
     * @param file
     * @return
     * @throws IOException
     */
    public HView<? extends View> parse(File file) throws IOException{
        try {
            String template = hTemplate.apply(new FileInputStream(file));
            return inflater(template);
        } catch (IOException e) {
            throw e;
        }
    }

    public HTemplate getTemplate() {
        return hTemplate;
    }

    /**
     *
     * 设置回调
     * @param onLayoutInflaterListener
     */
    public void setOnLayoutInflaterListener(LayoutInflaterListener onLayoutInflaterListener) {
        this.onLayoutInflaterListener = onLayoutInflaterListener;
    }

    void checkLayoutInflaterListenerSetted() {
        if(onLayoutInflaterListener == null) {
            throw new HLayoutException("not exec setOnLayoutInflaterListener method");
        }
    }
}
