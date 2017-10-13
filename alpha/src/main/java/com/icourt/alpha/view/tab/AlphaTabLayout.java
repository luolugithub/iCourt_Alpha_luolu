package com.icourt.alpha.view.tab;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.abs.IPagerNavigator;

/**
 * Description  监听viewpager动态添加数据与适配器
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/24
 * version 2.1.0
 */
public class AlphaTabLayout extends MagicIndicator
        implements ViewPager.OnAdapterChangeListener,
        ViewPager.OnPageChangeListener {
    ViewPager mViewPager;
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateNavigator();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            updateNavigator();
        }

        private void updateNavigator() {
            if (getNavigator() != null) {
                getNavigator().notifyDataSetChanged();
            }
        }
    };

    public AlphaTabLayout(Context context) {
        super(context);
    }

    public AlphaTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
        if (oldAdapter != null) {
            oldAdapter.registerDataSetObserver(dataSetObserver);
        }
        addSingleDataSetObserver(newAdapter);
    }


    public void setupWithViewPager(@NonNull ViewPager viewPager) {
        if (viewPager == null) return;
        mViewPager = viewPager;
        addSinglePageChangeListener(viewPager);
        addSingleAdapterChangeListener(viewPager);
        if (viewPager.getAdapter() != null) {
            addSingleDataSetObserver(viewPager.getAdapter());
        }
    }

    private void addSinglePageChangeListener(@NonNull ViewPager viewPager) {
        if (viewPager == null) return;
        viewPager.removeOnPageChangeListener(this);
        viewPager.addOnPageChangeListener(this);
    }

    private void addSingleAdapterChangeListener(@NonNull ViewPager viewPager) {
        if (viewPager == null) return;
        try {
            viewPager.removeOnAdapterChangeListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewPager.addOnAdapterChangeListener(this);
    }

    private void addSingleDataSetObserver(@NonNull PagerAdapter pagerAdapter) {
        if (pagerAdapter == null) return;
        try {
            pagerAdapter.unregisterDataSetObserver(dataSetObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pagerAdapter.registerDataSetObserver(dataSetObserver);
    }

    @CallSuper
    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
    }

    @CallSuper
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @CallSuper
    @Override
    public void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);
    }

    @Override
    public void setNavigator(IPagerNavigator navigator) {
        super.setNavigator(navigator);
    }

    /**
     * 设置指示器
     *
     * @param navigator
     * @return
     */
    public AlphaTabLayout setNavigator2(IPagerNavigator navigator) {
        setNavigator(navigator);
        return this;
    }
}
