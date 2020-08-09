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

import java.lang.reflect.Method;
import java.util.Set;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;


public class hook implements IXposedHookLoadPackage {
    private static SharedPreferences sharedPreferences;

    private static Class<?> clazz;

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


    private static class HookedMethod extends XC_MethodHook {
        private interface After {
            void afterHookedMethod(MethodHookParam param);
        }

        private interface Before {
            void afterHookedMethod(MethodHookParam param);
        }

        private static XC_MethodHook after(After after) {
            return new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    after.afterHookedMethod(param);
                }
            };
        }

        private static XC_MethodHook before(Before before) {
            return new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    before.afterHookedMethod(param);
                }
            };
        }
    }

    private void changeView() {
        if (mOperationBtn != null) {
            ((RelativeLayout.LayoutParams) mOperationBtn.getLayoutParams()).setMarginEnd(dip2px(margin));
        }
        if (mPlayBtn != null) {
            RelativeLayout.LayoutParams new_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, dip2px(49));
            new_lp.addRule(RelativeLayout.LEFT_OF, mOperationBtn.getId());
            mPlayBtn.setLayoutParams(new_lp);
        }
////
        if (mAlbumView != null) {
            if (mAlbumView.getParent() != null) {
                mAlbumView.setX(dip2px(margin));
            } else {
                mAlbumView.setX(mMusicNameView.getX() + dip2px(margin) - mAlbumView.getWidth());
            }
        }
        if (mMusicNameView != null) {
            mMusicNameView.setPadding(dip2px(margin), 0, 0, 0);
        }
        if (mArtistNameView != null) {
            mArtistNameView.setPadding(dip2px(margin), 0, 0, 0);
        }
////
        if (mAlbumScrollView != null) {
            if (mAlbumScrollView.getParent() != null) {
                mAlbumScrollView.setX(dip2px(margin));
            } else {
                mAlbumScrollView.setX(mMusicNameScrollView.getX() + dip2px(margin) - mAlbumScrollView.getWidth());
            }
        }
        if (mMusicNameScrollView != null) {
            mMusicNameScrollView.setPadding(dip2px(margin), 0, 0, 0);
        }
        if (mArtistNameScrollView != null) {
            mArtistNameScrollView.setPadding(dip2px(margin), 0, 0, 0);
        }
    }

    public void handleLoadPackage(final LoadPackageParam lPParam) {
        switch (lPParam.packageName) {
            case "com.netease.cloudmusic":
                hookCloudMusic(lPParam);
                break;
            case "xyz.kymirai.cloudmusic":
                hookSelf(lPParam);
                break;
        }
    }

    private void hookCloudMusic(final LoadPackageParam lPParam) {
        findAndHookMethod("com.netease.nis.wrapper.MyApplication", lPParam.classLoader, "onCreate", HookedMethod.after(param -> {
            if (getClazz(lPParam.classLoader) == null) return;

            findAndHookMethod(clazz, "showMinPlayerBar", boolean.class, HookedMethod.after(param1 -> {
                margin = getSharedPreferences().getInt("margin", 12);

                //播放列表
                mOperationBtn = (ImageView) XposedHelpers.getObjectField(param1.thisObject, "mOperationBtn");

                //播放/暂停按钮
                mPlayBtn = (ImageView) XposedHelpers.getObjectField(param1.thisObject, "mPlayBtn");

                //底部播放条
                ViewGroup mMiniPlayBarInfoLayout = (ViewGroup) XposedHelpers.getObjectField(param1.thisObject, "mMiniPlayBarInfoLayout");

                if (mMiniPlayBarInfoLayout != null) {
                    mAlbumView = (ImageView) XposedHelpers.getObjectField(mMiniPlayBarInfoLayout, "mAlbumView");
                    mMusicNameView = (View) XposedHelpers.getObjectField(mMiniPlayBarInfoLayout, "mMusicNameView");
                    mArtistNameView = (View) XposedHelpers.getObjectField(mMiniPlayBarInfoLayout, "mArtistNameView");

                    mAlbumScrollView = (View) XposedHelpers.getObjectField(mMiniPlayBarInfoLayout, "mAlbumScrollView");
                    mMusicNameScrollView = (View) XposedHelpers.getObjectField(mMiniPlayBarInfoLayout, "mMusicNameScrollView");
                    mArtistNameScrollView = (View) XposedHelpers.getObjectField(mMiniPlayBarInfoLayout, "mArtistNameScrollView");
                }

                changeView();
            }));

            findAndHookConstructor("com.netease.cloudmusic.ui.MiniPlayBarInfoLayout", lPParam.classLoader, Context.class, AttributeSet.class, HookedMethod.after(param1 -> {
                mAlbumView = (ImageView) XposedHelpers.getObjectField(param1.thisObject, "mAlbumView");
                mMusicNameView = (View) XposedHelpers.getObjectField(param1.thisObject, "mMusicNameView");
                mArtistNameView = (View) XposedHelpers.getObjectField(param1.thisObject, "mArtistNameView");

                mAlbumScrollView = (View) XposedHelpers.getObjectField(param1.thisObject, "mAlbumScrollView");
                mMusicNameScrollView = (View) XposedHelpers.getObjectField(param1.thisObject, "mMusicNameScrollView");
                mArtistNameScrollView = (View) XposedHelpers.getObjectField(param1.thisObject, "mArtistNameScrollView");
                changeView();
            }));

            findAndHookMethod("com.netease.cloudmusic.ui.MiniPlayBarInfoLayout", lPParam.classLoader, "applyScrollViewCurrentTheme", HookedMethod.before(param1 -> {
                margin = getSharedPreferences().getInt("margin", 12);

                mAlbumScrollView = (View) XposedHelpers.getObjectField(param1.thisObject, "mAlbumScrollView");
                if (mAlbumScrollView != null) {
                    mAlbumScrollView.setX(dip2px(margin));
                }

                mMusicNameScrollView = (View) XposedHelpers.getObjectField(param1.thisObject, "mMusicNameScrollView");
                if (mMusicNameScrollView != null) {
                    mMusicNameScrollView.setPadding(dip2px(margin), 0, 0, 0);
                }

                mArtistNameScrollView = (View) XposedHelpers.getObjectField(param1.thisObject, "mArtistNameScrollView");
                if (mArtistNameScrollView != null) {
                    mArtistNameScrollView.setPadding(dip2px(margin), 0, 0, 0);
                }
            }));

            //底部播放栏长按事件
            findAndHookMethod("com.netease.cloudmusic.ui.MiniPlayBarInfoLayout", lPParam.classLoader, "onLongPress", MotionEvent.class, HookedMethod.after(param1 -> {
                Context context = ((View) param1.thisObject).getContext();
                showController(context);
            }));
        }));
    }

    private static void hookSelf(final LoadPackageParam lPParam) {
        findAndHookMethod("xyz.kymirai.cloudmusic.MainActivity", lPParam.classLoader, "isActive", XC_MethodReplacement.returnConstant(true));
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

    private static Class<?> getClazz(ClassLoader loader) {
        if (clazz == null) {
            Set<String> classes = ClassUtils.getClassName(loader, "com.netease.cloudmusic.activity", false);
            for (String clazzName : classes) {
                Method method = XposedHelpers.findMethodExactIfExists(clazzName, loader, "showMinPlayerBar", boolean.class);
                if (method != null) clazz = method.getDeclaringClass();
            }
        }
        return clazz;
    }
}