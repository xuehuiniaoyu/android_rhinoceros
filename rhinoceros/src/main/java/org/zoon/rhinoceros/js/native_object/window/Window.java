package org.zoon.rhinoceros.js.native_object.window;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.zoon.rhinoceros.js.native_object.NativeObjInfo;
import org.zoon.rhinoceros.utils.IDUtil;

/**
 *
 * 为js提供一些工具函数
 */
public class Window extends NativeObjInfo {
    public Window(Context context) {
        super(context);
    }

    /**
     * 获取 SystemService
     * @return
     */
    public Object getSystemService() {
        return getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 获取显示指标
     * 屏幕信息
     * @return
     */
    public DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }
}
