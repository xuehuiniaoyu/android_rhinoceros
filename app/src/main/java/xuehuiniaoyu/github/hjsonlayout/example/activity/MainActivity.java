package xuehuiniaoyu.github.hjsonlayout.example.activity;

import android.os.Bundle;

import org.zoon.rhinoceros.app.HActivity;

public class MainActivity extends HActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewFromAssets("main.hj");
    }
}
