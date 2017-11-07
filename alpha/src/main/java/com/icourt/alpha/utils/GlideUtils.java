package com.icourt.alpha.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.icourt.alpha.R;
import com.icourt.alpha.utils.transformations.GlideCircleTransform;

/**
 * Description
 * Glide 工具类
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：16/7/11
 * version
 */

public class GlideUtils {


    /**
     * fragment 中 glide是否可以加载图片
     * 否则 glide会引发崩溃
     *
     * @param fragment
     * @return
     */
    public static boolean canLoadImage(Fragment fragment) {
        if (fragment == null) {
            return false;
        }
        FragmentActivity parentActivity = fragment.getActivity();
        return canLoadImage(parentActivity);
    }

    /**
     * context 中 glide是否可以加载图片
     * 否则 glide会引发崩溃
     *
     * @param context
     * @return
     */
    public static boolean canLoadImage(Context context) {
        if (context == null) {
            return false;
        }
        if (!(context instanceof Activity)) {
            return true;
        }
        Activity activity = (Activity) context;
        return canLoadImage(activity);
    }

    /**
     * activity 中 glide是否可以加载图片
     * 否则 glide会引发崩溃
     *
     * @param activity
     * @return
     */
    public static boolean canLoadImage(Activity activity) {
        return !SystemUtils.isDestroyOrFinishing(activity);
    }


    private GlideUtils() {
    }

    /**
     * 加载用户 头像 等 圆角
     * 追加token
     *
     * @param context
     * @param path
     * @param imageView
     */
    public static void loadUser(Context context, String path, ImageView imageView) {
        if (context == null) return;
        if (imageView == null) return;
        if (canLoadImage(context)) {
            Glide.with(context)
                    .load(path)
                    .thumbnail(0.2f)
                    .transform(new GlideCircleTransform(context))
                    .placeholder(R.mipmap.avatar_default_80)
                    .error(R.mipmap.avatar_default_80)
                    .dontAnimate()
                    .into(imageView);
        }
    }

    /**
     * 加载用户
     *
     * @param path
     * @param imageView
     * @param placeholderDrawable
     * @param errorDrawable
     */
    public static void loadUser(String path,
                                ImageView imageView,
                                @DrawableRes int placeholderDrawable,
                                @DrawableRes int errorDrawable) {
        if (imageView == null) {
            return;
        }
        if (canLoadImage(imageView.getContext())) {
            Glide.with(imageView.getContext())
                    .load(path)
                    .thumbnail(0.2f)
                    .transform(new GlideCircleTransform(imageView.getContext()))
                    .placeholder(placeholderDrawable)
                    .error(errorDrawable)
                    .dontAnimate()
                    .into(imageView);
        }
    }

    /**
     * 加载alpha小助手用户
     *
     * @param path
     * @param imageView
     */
    public static void loadAlphaSpecialUser(String path,
                                            ImageView imageView) {
        loadUser(path, imageView, R.mipmap.alpha_assistant_20, R.mipmap.alpha_assistant_20);
    }

    /**
     * 加载用户 头像 等 圆角
     *
     * @param context
     * @param path
     * @param imageView
     */
    public static void loadGroup(Context context, String path, ImageView imageView) {
        if (context == null) return;
        if (imageView == null) return;
        if (canLoadImage(context)) {
            Glide.with(context)
                    .load(path)
                    .thumbnail(0.2f)
                    // .transform(new GlideCircleTransform(context))
                    .placeholder(R.mipmap.ic_group_icon_default)
                    .error(R.mipmap.ic_group_icon_default)
                    .crossFade()
                    .into(imageView);
        }
    }


    /**
     * @param context
     * @param path
     * @param imageView
     */
    public static void loadPic(Context context, String path, ImageView imageView) {
        if (imageView == null) return;
        if (canLoadImage(context)) {
            Glide.with(context)
                    .load(path)
                    .error(R.mipmap.default_img_failed)
                    .centerCrop()
                    .into(imageView);

        }
    }

    /**
     * @param context
     * @param path
     * @param imageView
     */
    public static void loadSFilePic(Context context, String path, ImageView imageView) {
        if (imageView == null) return;
        if (canLoadImage(context)) {
            Glide.with(context)
                    .load(path)
                    .placeholder(R.mipmap.filetype_image)
                    .error(R.mipmap.filetype_image)
                    .into(imageView);
        }
    }

    /**
     * 适合本地加载  必须s设置  .diskCacheStrategy(DiskCacheStrategy.SOURCE)
     * 否则本地gif有问题
     *
     * @param context
     * @param path
     * @param imageView
     */
    public static void loadSFilePicWithoutPlaceholder(Context context, String path, ImageView imageView) {
        if (imageView == null) return;
        if (canLoadImage(context)) {
            Glide.with(context)
                    .load(path)
                    .error(R.mipmap.filetype_image)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .dontAnimate()
                    .into(imageView);
        }
    }

    /**
     * 为图片着色
     *
     * @param context
     * @param drawableRes
     * @param color
     * @return
     */
    public static Drawable getTintedDrawable(Context context, int drawableRes, int color) {
        Drawable drawable = null;
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = context.getResources().getDrawable(drawableRes, null);
                if (drawable != null) {
                    drawable.setTint(color);
                }
            } else {
                drawable = context.getResources().getDrawable(drawableRes);
                if (drawable != null) {
                    drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                }
            }
        }
        return drawable;
    }


    /**
     * 为图片着色
     * <p>
     * <?xml version="1.0" encoding="utf-8"?>
     * <selector xmlns:android="http://schemas.android.com/apk/res/android">
     * <item android:color="#FF4081" android:state_pressed="true" />
     * <item android:color="#3F51B5" />
     * </selector>
     * <p>
     * eg.  ivButton.setImageDrawable(tintDrawable(src,ColorStateList.valueOf(Color.WHITE)));
     *
     * @param drawable
     * @param colors
     * @return
     */
    public static Drawable getTintedDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

}

