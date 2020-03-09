package xyz.kymirai.cloudmusic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Switch;

public class MainActivity extends Activity {
    private SharedPreferences sharedPreferences;

    SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch hide = findViewById(R.id.hide);
        hide.setText(Html.fromHtml(getResources().getString(R.string.switch_hide)));
        hide.setOnClickListener(this::onClick);
        hide.setChecked(getSharedPreferences().getBoolean("hide", false));

        Switch active = findViewById(R.id.active);
        active.setChecked(isActive());
        active.setOnCheckedChangeListener((v, b) -> {
            v.setChecked(isActive());
        });
    }

    public void onClick(View v) {
        ComponentName componentName = new ComponentName(MainActivity.this, "xyz.kymirai.cloudmusic.MainActivity");
        if (((Switch) v).isChecked()) {
            getPackageManager().setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            getPackageManager().setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                    PackageManager.DONT_KILL_APP);
        }
        getSharedPreferences().edit().putBoolean("hide", ((Switch) v).isChecked()).apply();
    }

    private boolean isActive() {
        return false;
    }
}
