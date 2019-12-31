package xyz.kymirai.cloudmusic;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.app.AndroidAppHelper;
import android.view.ViewGroup;


public class hook implements IXposedHookLoadPackage {
    private static int MarginStart = 18;
    private static int MarginEnd = 18;

    public void handleLoadPackage(final LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.netease.cloudmusic")) {
            XposedHelpers.findAndHookMethod("com.netease.cloudmusic.activity.n", lpparam.classLoader, "findViews", new XC_MethodHook() {
                @Override
                public void afterHookedMethod(MethodHookParam p) throws Throwable {
                    //播放列表
                    ImageView mOperationBtn = (ImageView) XposedHelpers.getObjectField(p.thisObject, "mOperationBtn");
                    ((RelativeLayout.LayoutParams) mOperationBtn.getLayoutParams()).setMarginEnd(dip2px(MarginEnd));


                    //播放/暂停按钮
                    ImageView mPlayBtn = (ImageView) XposedHelpers.getObjectField(p.thisObject, "mPlayBtn");
                    mPlayBtn.setX(mPlayBtn.getX() - dip2px(MarginEnd));

                    //底部播放条
                    ViewGroup mMiniPlayBarInfoLayout = (ViewGroup) XposedHelpers.getObjectField(p.thisObject, "mMiniPlayBarInfoLayout");
                    // mMiniPlayBarInfoLayout.setPadding(0, 0, dip2px(MarginEnd), 0);
                    ((RelativeLayout.LayoutParams) mMiniPlayBarInfoLayout.getLayoutParams()).setMarginEnd(dip2px(MarginEnd));
                }
            });

            XposedHelpers.findAndHookConstructor("com.netease.cloudmusic.ui.MiniPlayBarInfoLayout", lpparam.classLoader, Context.class, AttributeSet.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    View mAlbumView = (View) XposedHelpers.getObjectField(param.thisObject, "mAlbumView");
                    mAlbumView.setX(dip2px(MarginStart));

                    View mMusicNameView = (View) XposedHelpers.getObjectField(param.thisObject, "mMusicNameView");
                    mMusicNameView.setX(mMusicNameView.getX() + dip2px(MarginStart));

                    View mArtistNameView = (View) XposedHelpers.getObjectField(param.thisObject, "mArtistNameView");
                    mArtistNameView.setX(mArtistNameView.getX() + dip2px(MarginStart));
                }
            });

//            XposedHelpers.findAndHookMethod("com.netease.cloudmusic.ui.MiniPlayBarInfoLayout", lpparam.classLoader, "initScrollView", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    View mAlbumScrollView = (View) XposedHelpers.getObjectField(param.thisObject, "mAlbumScrollView");
//                    if (mAlbumScrollView != null)
//                        mAlbumScrollView.setX(mAlbumScrollView.getX() + dip2px(MarginStart));
//
//                    View mMusicNameScrollView = (View) XposedHelpers.getObjectField(param.thisObject, "mMusicNameScrollView");
//                    mMusicNameScrollView.setX(mMusicNameScrollView.getX() + dip2px(MarginStart));
//
//                    View mArtistNameScrollView = (View) XposedHelpers.getObjectField(param.thisObject, "mArtistNameScrollView");
//                    mArtistNameScrollView.setX(mArtistNameScrollView.getX() + dip2px(MarginStart));
//                }
//            });

            XposedHelpers.findAndHookMethod("com.netease.cloudmusic.ui.MiniPlayBarInfoLayout", lpparam.classLoader, "applyScrollViewCurrentTheme", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    View mAlbumScrollView = (View) XposedHelpers.getObjectField(param.thisObject, "mAlbumScrollView");
                    if (mAlbumScrollView != null)
                        mAlbumScrollView.setX(mAlbumScrollView.getX() + dip2px(MarginStart));

                    View mMusicNameScrollView = (View) XposedHelpers.getObjectField(param.thisObject, "mMusicNameScrollView");
                    if (mMusicNameScrollView != null)
                        mMusicNameScrollView.setX(mMusicNameScrollView.getX() + dip2px(MarginStart));

                    View mArtistNameScrollView = (View) XposedHelpers.getObjectField(param.thisObject, "mArtistNameScrollView");
                    if (mArtistNameScrollView != null)
                        mArtistNameScrollView.setX(mArtistNameScrollView.getX() + dip2px(MarginStart));
                }
            });
        }
    }

    private static int dip2px(float dpValue) {
        final float scale = AndroidAppHelper.currentApplication().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
