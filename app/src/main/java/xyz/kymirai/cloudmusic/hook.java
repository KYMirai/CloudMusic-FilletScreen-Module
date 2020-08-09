package xyz.kymirai.cloudmusic;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.app.AndroidAppHelper;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class hook implements IXposedHookLoadPackage {
    private static SharedPreferences sharedPreferences;

    private static int margin = 12;

    private ImageView mOperationBtn;//播放列表
    private ImageView mPlayBtn;//播放/暂停按钮
    //private ViewGroup mMiniPlayBarInfoLayout;//底部播放条

    private ImageView mAlbumView;//专辑封面iv
    private View mMusicNameView;//歌名tv
    private View mArtistNameView;//歌词tv

    private View mAlbumScrollView;//专辑封面iv
    private View mMusicNameScrollView;//歌名tv
    private View mArtistNameScrollView;//歌词tv

    private interface Hooked {
        void hooked(XC_MethodHook.MethodHookParam param);
    }

    private static XC_MethodHook after(Hooked after) {
        return new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                after.hooked(param);
            }
        };
    }

    private static XC_MethodHook before(Hooked before) {
        return new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                before.hooked(param);
            }
        };
    }

    private void changeView() {
        try {
            ((RelativeLayout.LayoutParams) mOperationBtn.getLayoutParams()).rightMargin = dip2px(margin);
            mOperationBtn.requestLayout();
////
            mAlbumView.setX((mAlbumView.getParent() == null ? mMusicNameView.getX() - mAlbumView.getWidth() : 0) + dip2px(margin));

            mMusicNameView.setPadding(dip2px(margin), 0, 0, 0);

            mArtistNameView.setPadding(dip2px(margin), 0, 0, 0);
////
            mAlbumScrollView.setX((mAlbumScrollView.getParent() == null ? mMusicNameScrollView.getX() - mAlbumScrollView.getWidth() : 0) + dip2px(margin));

            mMusicNameScrollView.setPadding(dip2px(margin), 0, 0, 0);

            mArtistNameScrollView.setPadding(dip2px(margin), 0, 0, 0);
        } catch (Throwable ignored) {
        }
    }

    public void handleLoadPackage(final LoadPackageParam lPParam) {
        switch (lPParam.packageName) {
            case "com.netease.cloudmusic":
                hookCloudMusic(lPParam);
                break;
            case "xyz.kymirai.cloudmusic":
                findAndHookMethod("xyz.kymirai.cloudmusic.MainActivity", lPParam.classLoader, "isActive", XC_MethodReplacement.returnConstant(true));
                break;
        }
    }

    private void initView(Object object) {
        if (object == null) return;

        mAlbumView = (ImageView) XposedHelpers.getObjectField(object, "mAlbumView");
        mMusicNameView = (View) XposedHelpers.getObjectField(object, "mMusicNameView");
        mArtistNameView = (View) XposedHelpers.getObjectField(object, "mArtistNameView");

        initScrollView(object);
    }

    private void initScrollView(Object object) {
        if (object == null) return;

        mAlbumScrollView = (View) XposedHelpers.getObjectField(object, "mAlbumScrollView");
        mMusicNameScrollView = (View) XposedHelpers.getObjectField(object, "mMusicNameScrollView");
        mArtistNameScrollView = (View) XposedHelpers.getObjectField(object, "mArtistNameScrollView");

        changeView();
    }

    private void hookCloudMusic(final LoadPackageParam lPParam) {
        Class<?> SuperClazz = XposedHelpers.findClass("com.netease.cloudmusic.activity.MainActivity", lPParam.classLoader).getSuperclass().getSuperclass();
        findAndHookMethod(SuperClazz, "showMinPlayerBar", boolean.class, after(param -> {
            margin = getSharedPreferences().getInt("margin", 12);

            //播放列表
            mOperationBtn = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mOperationBtn");
            RelativeLayout.LayoutParams mOperationBtnLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mOperationBtnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            mOperationBtn.setLayoutParams(mOperationBtnLayoutParams);

            //播放/暂停按钮
            mPlayBtn = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mPlayBtn");
            RelativeLayout.LayoutParams mPlayBtnLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mPlayBtnLayoutParams.addRule(RelativeLayout.LEFT_OF, mOperationBtn.getId());
            mPlayBtn.setLayoutParams(mPlayBtnLayoutParams);

            //底部播放条
            //ViewGroup mMiniPlayBarInfoLayout = (ViewGroup) XposedHelpers.getObjectField(param.thisObject, "mMiniPlayBarInfoLayout");

            initView(XposedHelpers.getObjectField(param.thisObject, "mMiniPlayBarInfoLayout"));
        }));

        findAndHookMethod(SuperClazz, "changMiniPlayerBar", boolean.class, int.class, after(param -> {
            ((RelativeLayout.LayoutParams) mPlayBtn.getLayoutParams()).rightMargin = 0;
            mPlayBtn.requestLayout();
        }));

        findAndHookConstructor("com.netease.cloudmusic.ui.MiniPlayBarInfoLayout", lPParam.classLoader,
                Context.class, AttributeSet.class, after(param -> initView(param.thisObject)));

        findAndHookMethod("com.netease.cloudmusic.ui.MiniPlayBarInfoLayout", lPParam.classLoader,
                "applyScrollViewCurrentTheme", before(param -> initScrollView(param.thisObject)));

        //底部播放栏长按事件
        findAndHookMethod("com.netease.cloudmusic.ui.MiniPlayBarInfoLayout", lPParam.classLoader,
                "onLongPress", MotionEvent.class, after(param -> showController(((View) param.thisObject).getContext())));
    }

    private void showController(Context context) {
        RelativeLayout rl = new RelativeLayout(context);
        rl.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final TextView textView = new TextView(context);
        textView.setText(String.valueOf(margin));
        textView.setId(R.id.textView);

        SeekBar seekBar = new SeekBar(context);
        seekBar.setMax(100);
        seekBar.setProgress(margin);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                margin = progress;
                textView.setText(String.valueOf(progress));
                getSharedPreferences().edit().putInt("margin", margin).apply();
                changeView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        RelativeLayout.LayoutParams lp_tv = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_tv.setMarginEnd(dip2px(12));
        lp_tv.addRule(RelativeLayout.CENTER_VERTICAL);
        lp_tv.addRule(RelativeLayout.ALIGN_PARENT_END);
        rl.addView(textView, lp_tv);

        RelativeLayout.LayoutParams lp_seekBar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp_seekBar.setMargins(dip2px(12), dip2px(12), 0, dip2px(12));
        lp_seekBar.addRule(RelativeLayout.START_OF, R.id.textView);
        lp_seekBar.addRule(RelativeLayout.CENTER_VERTICAL);
        rl.addView(seekBar, lp_seekBar);

        Dialog dialog = new Dialog(context);
        dialog.setContentView(rl);
        dialog.show();
    }

    private static int dip2px(float dpValue) {
        final float scale = AndroidAppHelper.currentApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            synchronized (hook.class) {
                if (sharedPreferences == null) {
                    sharedPreferences = AndroidAppHelper.currentApplication().getSharedPreferences("setting", Context.MODE_PRIVATE);
                }
            }
        }
        return sharedPreferences;
    }
}