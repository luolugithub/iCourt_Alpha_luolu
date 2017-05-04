package com.icourt.alpha.utils.transformations;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.target.BitmapImageViewTarget;

/**
 * Description 根据控件的宽度 按比例进行高度自适应
 * Company Beijing guokeyuzhou
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：16/12/24
 * version
 */

public class FitHeightImgViewTarget extends BitmapImageViewTarget {

    public FitHeightImgViewTarget(ImageView view) {
        super(view);
    }

    @Override
    protected void setResource(Bitmap resource) {
        View target = getView();
        if (target != null && target.getWidth() > 0 &&
                resource != null && resource.getWidth() > 0) {
            float ratio = (resource.getHeight() * 1.0f) / resource.getWidth();
            if (ratio > 0) {
                ViewGroup.LayoutParams layoutParams = target.getLayoutParams();
                int calculateHeight = (int) (ratio * target.getWidth());
                if (layoutParams != null) {
                    if (layoutParams.height != calculateHeight) {
                        layoutParams.height = calculateHeight;
                        target.setLayoutParams(layoutParams);
                    }
                }
            }
        }
        super.setResource(resource);
    }
}
