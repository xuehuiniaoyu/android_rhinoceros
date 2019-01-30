package xuehuiniaoyu.github.hjsonlayout.example.activity;

import android.os.Bundle;
import android.widget.TextView;

import org.zoon.rhinoceros.app.HActivity;

import xuehuiniaoyu.github.hjsonlayout.example.dialog.FinishDialog;

public class HelloWorldActivity extends HActivity {
    private FinishDialog finishDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewFromAssets("hello_world.hj");
    }

    public void showDialog(TextView view) {
        if(finishDialog != null) {
            finishDialog.dismiss();
        }
        finishDialog = new FinishDialog(HelloWorldActivity.this, "确定", "取消", "对话框");
        finishDialog.show("弹出对话框！");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(finishDialog != null) {
            finishDialog.dismiss();
            finishDialog = null;
        }
    }
}