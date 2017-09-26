package com.icourt.alpha.view.tab;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.icourt.alpha.R;
import com.icourt.alpha.view.tab.pagertitleview.ScaleTransitionPagerTitleView;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

/**
 * Description  标准头部导航
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/24
 * version 2.1.0
 */
public abstract class AlphaTitleNavigatorAdapter extends BaseNavigatorAdapter implements OnTabClickListener {

    public AlphaTitleNavigatorAdapter() {

    }

    float mMinScale = 0.90f;

    /**
     * @param minScale 最小缩放倍数
     */
    public AlphaTitleNavigatorAdapter(@FloatRange(from = 0.0f, to = 1.0f) float minScale) {
        this.mMinScale = minScale;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        ScaleTransitionPagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
        simplePagerTitleView.setMinScale(mMinScale);
        simplePagerTitleView.setText(getTitle(index));
        simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.alpha_tab_title_txt_size));
        simplePagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.alpha_tab_title_txt_color));
        simplePagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.alpha_tab_title_txt_selected_color));
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabClick(v, index);
            }
        });
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setStartInterpolator(new AccelerateInterpolator());
        indicator.setEndInterpolator(new DecelerateInterpolator(1.8f));
        indicator.setLineHeight(context.getResources().getDimension(R.dimen.alpha_tab_title_indicator_height));
        indicator.setColors(ContextCompat.getColor(context, R.color.alpha_tab_title_indicator_color));
        return indicator;
    }
}
