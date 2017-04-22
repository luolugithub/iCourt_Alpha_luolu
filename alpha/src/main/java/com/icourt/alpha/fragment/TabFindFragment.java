package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.ItemsEntity;
import com.icourt.alpha.utils.SpUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

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

    /**
     * 生成菜单数据
     *
     * @param tabFindFragment
     * @return
     */
    public static List<ItemsEntity> generateMenuData(@NonNull TabFindFragment tabFindFragment) {
        if (tabFindFragment != null) {
            switch (tabFindFragment.getShowChildFragmentType()) {
                case TYPE_FRAGMENT_PROJECT:
                    return Arrays.asList(new ItemsEntity("计时", TYPE_FRAGMENT_TIMING, R.mipmap.tab_task),
                            new ItemsEntity("客户", TYPE_FRAGMENT_CUSTOMER, R.mipmap.tab_my),
                            new ItemsEntity("搜索", TYPE_FRAGMENT_SEARCH, R.mipmap.tab_explorer));
                case TYPE_FRAGMENT_TIMING:
                    return Arrays.asList(new ItemsEntity("项目", TYPE_FRAGMENT_PROJECT, R.mipmap.tab_message),
                            new ItemsEntity("客户", TYPE_FRAGMENT_CUSTOMER, R.mipmap.tab_my),
                            new ItemsEntity("搜索", TYPE_FRAGMENT_SEARCH, R.mipmap.tab_explorer));
                case TYPE_FRAGMENT_CUSTOMER:
                    return Arrays.asList(new ItemsEntity("项目", TYPE_FRAGMENT_PROJECT, R.mipmap.tab_message),
                            new ItemsEntity("计时", TYPE_FRAGMENT_TIMING, R.mipmap.tab_task),
                            new ItemsEntity("搜索", TYPE_FRAGMENT_SEARCH, R.mipmap.tab_explorer));
                case TYPE_FRAGMENT_SEARCH:
                    return Arrays.asList(new ItemsEntity("项目", TYPE_FRAGMENT_PROJECT, R.mipmap.tab_message),
                            new ItemsEntity("计时", TYPE_FRAGMENT_TIMING, R.mipmap.tab_task),
                            new ItemsEntity("客户", TYPE_FRAGMENT_CUSTOMER, R.mipmap.tab_my));
            }
        }
        return null;
    }

    public static String KEY_TYPE_FRAGMENT = "type_TabFindFragment_fragment";

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

    /**
     * 类型转化
     *
     * @param type
     * @return
     */
    @ChildFragmentType
    public static final int convert2ChildFragmentType(int type) {
        switch (type) {
            case TYPE_FRAGMENT_PROJECT:
                return TYPE_FRAGMENT_PROJECT;
            case TYPE_FRAGMENT_TIMING:
                return TYPE_FRAGMENT_TIMING;
            case TYPE_FRAGMENT_CUSTOMER:
                return TYPE_FRAGMENT_CUSTOMER;
            case TYPE_FRAGMENT_SEARCH:
                return TYPE_FRAGMENT_SEARCH;
        }
        return TYPE_FRAGMENT_PROJECT;
    }

    @Override
    protected void initView() {
        showFragment(convert2ChildFragmentType(getShowChildFragmentType()));
    }

    private void showFragment(@ChildFragmentType int type) {
        currentFragment = addOrShowFragment(getTabFragment(type), currentFragment, R.id.tab_find_frame);
        saveChildFragmentType(type);
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
     * 正在显示的child
     *
     * @return
     */
    public static int getShowChildFragmentType() {
        return SpUtils.getInstance().getIntData(KEY_TYPE_FRAGMENT, 0);
    }

    /**
     * 上次显示的child
     *
     * @return
     */
    @ChildFragmentType
    public static int getLastChildFragmentType() {
        return convert2ChildFragmentType(SpUtils.getInstance().getIntData(KEY_TYPE_FRAGMENT, 0));
    }

    private static void saveChildFragmentType(@ChildFragmentType int type) {
        SpUtils.getInstance().putData(KEY_TYPE_FRAGMENT, type);
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
    public void notifyFragmentUpdate(Fragment targetFrgament, int type, Bundle bundle) {
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
