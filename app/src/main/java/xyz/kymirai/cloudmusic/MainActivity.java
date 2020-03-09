package xyz.kymirai.cloudmusic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.instruction).setOnClickListener(this::onClick);
    }

    public void onClick(View v) {

    }
}
