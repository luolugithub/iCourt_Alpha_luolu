package com.icourt.alpha.view.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.icourt.alpha.R;

import java.lang.ref.WeakReference;

/**
 * Description
 * Company Beijing icourt
 * author zhaodanyang E-mail:zhaodanyang@icourt.cc
 * date createTime: 2017/10/12
 * version
 */

public class TimingListBehavior extends CoordinatorLayout.Behavior {

    private WeakReference<View> dependentView;

    public TimingListBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
//        if (dependency != null && dependency.getId() == R.id.timing_head) {
//            dependentView = new WeakReference<>(dependency);
//            return true;
//        }
        return false;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        child.layout(0, getDependentView().getBottom(), parent.getWidth(), getDependentView().getBottom() + child.getHeight());
        return true;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        child.setTranslationY(dependency.getHeight() + dependency.getTranslationY());
        return true;
    }

    private View getDependentView() {
        return dependentView.get();
    }
}
