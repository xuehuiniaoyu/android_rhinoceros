package org.zoon.rhinoceros.app;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.zoon.rhinoceros.layout.widget.HView;
import org.zoon.rhinoceros.parser.HLayoutInflater;
import org.zoon.rhinoceros.template.HTemplate;
import org.zoon.rhinoceros.template.SimpleHTemplate;
import org.zoon.rhinoceros.utils.FixMemLeak4Hw;
import org.zoon.rhinoceros.utils.IDUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HActivity extends Activity {
    private HLayoutInflater hLayoutInflater;
    private HTemplate hTemplate;
    private HView<? extends View> hView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hTemplate = new SimpleHTemplate();
        getInflater();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat( view, "alpha", 0.0F, 1.0F );
            fadeAnim.setDuration( 600 );
            fadeAnim.start();
        }
    }

    /**
     * 映射对象到为一个别名
     * 注意：这一步必须在 setContentView 调用前完成，否则不生效
     * @param key 别名
     * @param value 被映射对象
     */
    protected void as(String key, Object value) {
        hTemplate.as(key, value);
    }


    public HLayoutInflater getInflater() {
        if(hLayoutInflater == null) {
            hLayoutInflater = new HLayoutInflater(this, hTemplate);
        }
        return hLayoutInflater;
    }

    /**
     * 从Assets加载模板
     * @param templateName xxx.hjson
     */
    public void setContentViewFromAssets(String templateName) {
        try {
            hView = hLayoutInflater.parse(getAssets().open(templateName));
            setContentView(hView.getView());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 从File加载模板
     * @param file
     */
    public void setContentViewFromFile(String file) {
        setContentViewFromFile(new File(file));
    }


    /**
     * 从File加载模板
     * @param file
     */
    public void setContentViewFromFile(File file) {
        try {
            hView = hLayoutInflater.parse(new FileInputStream(file));
            setContentView(hView.getView());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从Raw加载模板
     * @param template
     */
    public void setContentViewFromRaw(int template) {
        try {
            hView = hLayoutInflater.parse(getResources().openRawResource(template));
            setContentView(hView.getView());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载模板
     * @param template
     */
    public void setContentView(String template) {
        try {
            hView = hLayoutInflater.parse(template);
            setContentView(hView.getView());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T extends View> T findViewById(String id) {
        return (T) findViewById(IDUtil.id(id));
    }

    protected HTemplate getHTemplate() {
        return hTemplate;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(hView != null) {
            hView.destroy();
            hView = null;
        }
        FixMemLeak4Hw.fixLeak(this);
    }

    public void startActivity(String className) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, className));
        startActivity(intent);
    }
}
