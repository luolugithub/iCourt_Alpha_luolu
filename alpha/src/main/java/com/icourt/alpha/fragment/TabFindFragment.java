package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description  发现tab页面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/8
 * version 1.0.0
 */
public class TabFindFragment extends BaseFragment {

    public static String KEY_TYPE_FRAGMENT = "type_fragment";

    public static final int TYPE_FRAGMENT_PROJECT = 0;
    public static final int TYPE_FRAGMENT_TIMING = 1;
    public static final int TYPE_FRAGMENT_CUSTOMER = 2;
    public static final int TYPE_FRAGMENT_SEARCH = 3;

    @IntDef({TYPE_FRAGMENT_PROJECT,
            TYPE_FRAGMENT_TIMING,
            TYPE_FRAGMENT_CUSTOMER,
            TYPE_FRAGMENT_SEARCH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ChildFragmentType {

    }

    Unbinder unbinder;
    @BindView(R.id.tab_find_frame)
    FrameLayout tabFindFrame;
    Fragment currentFragment;

    public static TabFindFragment newInstance() {
        return new TabFindFragment();
    }

    final SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_find, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        showFragment(TYPE_FRAGMENT_PROJECT);
    }

    private void showFragment(@ChildFragmentType int type) {
        currentFragment = addOrShowFragment(getTabFragment(type), currentFragment, R.id.tab_find_frame);
    }


    /**
     * 获取对应fragment
     *
     * @param type
     * @return
     */
    private Fragment getTabFragment(@ChildFragmentType int type) {
        Fragment fragment = fragmentSparseArray.get(type);
        if (fragment == null) {
            switch (type) {
                case TYPE_FRAGMENT_PROJECT:
                    putTabFragment(type, TabFindProjectFragment.newInstance());
                    break;
                case TYPE_FRAGMENT_TIMING:
                    putTabFragment(type, TabFindTimingFragment.newInstance());
                    break;
                case TYPE_FRAGMENT_CUSTOMER:
                    putTabFragment(type, TabFindCustomerFragment.newInstance());
                    break;
                case TYPE_FRAGMENT_SEARCH:
                    putTabFragment(type, TabFindSearchFragment.newInstance());
                    break;
            }
            return fragmentSparseArray.get(type);
        } else {
            return fragment;
        }
    }

    /**
     * 存放对应fragment
     *
     * @param checkedId
     * @param fragment
     */
    private void putTabFragment(@IdRes int checkedId, Fragment fragment) {
        fragmentSparseArray.put(checkedId, fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /**
     * 动态切换childFragment
     *
     * @param targetFrgament
     * @param bundle
     */
    @Override
    public void notifyFragmentUpdate(Fragment targetFrgament, Bundle bundle) {
        if (targetFrgament != TabFindFragment.this) return;
        if (bundle == null) return;
        switch (bundle.getInt(KEY_TYPE_FRAGMENT, -1)) {
            case TYPE_FRAGMENT_PROJECT:
                showFragment(TYPE_FRAGMENT_PROJECT);
                break;
            case TYPE_FRAGMENT_TIMING:
                showFragment(TYPE_FRAGMENT_TIMING);
                break;
            case TYPE_FRAGMENT_CUSTOMER:
                showFragment(TYPE_FRAGMENT_CUSTOMER);
                break;
            case TYPE_FRAGMENT_SEARCH:
                showFragment(TYPE_FRAGMENT_SEARCH);
                break;
        }
    }
}
