package xyz.kymirai.cloudmusic;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.DisplayCutout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.app.AndroidAppHelper;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


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

    private static SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = AndroidAppHelper.currentApplication().getSharedPreferences("setting", Context.MODE_PRIVATE);
        }
        return sharedPreferences;
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

    public void handleLoadPackage(final LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.netease.cloudmusic")) {
            XposedHelpers.findAndHookMethod("com.netease.cloudmusic.activity.n", lpparam.classLoader, "showMinPlayerBar", boolean.class, new XC_MethodHook() {
                @Override
                public void afterHookedMethod(MethodHookParam p) throws Throwable {
                    margin = getSharedPreferences().getInt("margin", 12);

                    //播放列表
                    mOperationBtn = (ImageView) XposedHelpers.getObjectField(p.thisObject, "mOperationBtn");

                    //播放/暂停按钮
                    mPlayBtn = (ImageView) XposedHelpers.getObjectField(p.thisObject, "mPlayBtn");

                    //底部播放条
                    ViewGroup mMiniPlayBarInfoLayout = (ViewGroup) XposedHelpers.getObjectField(p.thisObject, "mMiniPlayBarInfoLayout");

                    if (mMiniPlayBarInfoLayout != null) {
                        mAlbumView = (ImageView) XposedHelpers.getObjectField(mMiniPlayBarInfoLayout, "mAlbumView");
                        mMusicNameView = (View) XposedHelpers.getObjectField(mMiniPlayBarInfoLayout, "mMusicNameView");
                        mArtistNameView = (View) XposedHelpers.getObjectField(mMiniPlayBarInfoLayout, "mArtistNameView");

                        mAlbumScrollView = (View) XposedHelpers.getObjectField(mMiniPlayBarInfoLayout, "mAlbumScrollView");
                        mMusicNameScrollView = (View) XposedHelpers.getObjectField(mMiniPlayBarInfoLayout, "mMusicNameScrollView");
                        mArtistNameScrollView = (View) XposedHelpers.getObjectField(mMiniPlayBarInfoLayout, "mArtistNameScrollView");
                    }

                    changeView();
                }
            });

            XposedHelpers.findAndHookConstructor("com.netease.cloudmusic.ui.MiniPlayBarInfoLayout", lpparam.classLoader, Context.class, AttributeSet.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    margin = getSharedPreferences().getInt("margin", 12);

                    mAlbumView = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mAlbumView");
                    mAlbumView.setX(dip2px(margin));

                    mMusicNameView = (View) XposedHelpers.getObjectField(param.thisObject, "mMusicNameView");
                    mMusicNameView.setPadding(dip2px(margin), 0, 0, 0);

                    mArtistNameView = (View) XposedHelpers.getObjectField(param.thisObject, "mArtistNameView");
                    mArtistNameView.setPadding(dip2px(margin), 0, 0, 0);
                }
            });

            XposedHelpers.findAndHookMethod("com.netease.cloudmusic.ui.MiniPlayBarInfoLayout", lpparam.classLoader, "applyScrollViewCurrentTheme", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    margin = getSharedPreferences().getInt("margin", 12);

                    mAlbumScrollView = (View) XposedHelpers.getObjectField(param.thisObject, "mAlbumScrollView");
                    if (mAlbumScrollView != null) {
                        mAlbumScrollView.setX(dip2px(margin));
                    }

                    mMusicNameScrollView = (View) XposedHelpers.getObjectField(param.thisObject, "mMusicNameScrollView");
                    if (mMusicNameScrollView != null) {
                        mMusicNameScrollView.setPadding(dip2px(margin), 0, 0, 0);
                    }

                    mArtistNameScrollView = (View) XposedHelpers.getObjectField(param.thisObject, "mArtistNameScrollView");
                    if (mArtistNameScrollView != null) {
                        mArtistNameScrollView.setPadding(dip2px(margin), 0, 0, 0);
                    }
                }
            });

            XposedHelpers.findAndHookMethod("com.netease.cloudmusic.ui.MiniPlayBarInfoLayout", lpparam.classLoader, "onLongPress", MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Context context = ((View) param.thisObject).getContext();
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
                            getSharedPreferences().edit().putInt("margin", margin).commit();
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
            });
        }

        XposedHelpers.findAndHookMethod("android.view.WindowInsets", lpparam.classLoader, "getDisplayCutout", XC_MethodReplacement.returnConstant(XposedHelpers.getStaticObjectField(DisplayCutout.class, "NO_CUTOUT")));
        XposedHelpers.findAndHookMethod("com.android.internal.policy.DecorView", lpparam.classLoader, "onLayout", boolean.class, int.class, int.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                int left = (int) param.args[1];
                XposedBridge.log("滚滚滚" + left);
                left -= 20;
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log("滚滚滚" + param.args[1]);
            }
        });
    }

    private static int dip2px(float dpValue) {
        final float scale = AndroidAppHelper.currentApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}