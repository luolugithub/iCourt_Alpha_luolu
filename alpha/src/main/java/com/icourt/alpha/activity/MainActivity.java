package com.icourt.alpha.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseAppUpdateActivity;
import com.icourt.alpha.fragment.TabFindFragment;
import com.icourt.alpha.fragment.TabMineFragment;
import com.icourt.alpha.fragment.TabNewsFragment;
import com.icourt.alpha.fragment.TabTaskFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseAppUpdateActivity implements RadioGroup.OnCheckedChangeListener {


    @BindView(R.id.main_fl_content)
    FrameLayout mainFlContent;
    @BindView(R.id.tab_news)
    RadioButton tabNews;
    @BindView(R.id.tab_task)
    RadioButton tabTask;
    @BindView(R.id.tab_voice)
    ImageButton tabVoice;
    @BindView(R.id.tab_find)
    RadioButton tabFind;
    @BindView(R.id.tab_mine)
    RadioButton tabMine;
    @BindView(R.id.rg_main_tab)
    RadioGroup rgMainTab;
    private Fragment currentFragment;
    private final SparseArray<Fragment> fragmentSparseArray = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        rgMainTab.setOnCheckedChangeListener(this);
        addOrShowFragment(getTabFragment(rgMainTab.getCheckedRadioButtonId()));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        addOrShowFragment(getTabFragment(checkedId));
    }

    @OnClick({R.id.tab_voice})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_voice:
                showTopSnackBar("语音....");
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 获取对应fragment
     *
     * @param checkedId
     * @return
     */
    private Fragment getTabFragment(@IdRes int checkedId) {
        Fragment fragment = fragmentSparseArray.get(checkedId);
        if (fragment == null) {
            switch (checkedId) {
                case R.id.tab_news:
                    putTabFragment(checkedId, TabNewsFragment.newInstance());
                    break;
                case R.id.tab_task:
                    putTabFragment(checkedId, TabTaskFragment.newInstance());
                    break;
                case R.id.tab_find:
                    putTabFragment(checkedId, TabFindFragment.newInstance());
                    break;
                case R.id.tab_mine:
                    putTabFragment(checkedId, TabMineFragment.newInstance());
                    break;
            }
            return fragmentSparseArray.get(checkedId);
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


    /**
     * 添加或者显示碎片
     *
     * @param fragment
     */
    private void addOrShowFragment(Fragment fragment) {
        if (fragment == null) return;
        if (fragment == currentFragment) return;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (!fragment.isAdded()) { // 如果当前fragment添加，则添加到Fragment管理器中
            if (currentFragment == null) {
                transaction
                        .add(R.id.main_fl_content, fragment)
                        .commit();
            } else {
                transaction.hide(currentFragment)
                        .add(R.id.main_fl_content, fragment)
                        .commit();
            }
        } else {
            transaction
                    .hide(currentFragment)
                    .show(fragment)
                    .commit();
        }
        currentFragment = fragment;
    }


}
