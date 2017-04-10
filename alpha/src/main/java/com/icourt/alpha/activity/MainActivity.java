package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
import com.icourt.alpha.interfaces.OnTabDoubleClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseAppUpdateActivity implements RadioGroup.OnCheckedChangeListener {

    public static void launch(Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

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
    private GestureDetector tabGestureDetector;
    private GestureDetector.SimpleOnGestureListener tabGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Fragment currFragment = currentFragment;
            if (currFragment instanceof OnTabDoubleClickListener) {
                ((OnTabDoubleClickListener) currFragment).onTabDoubleClick(currFragment, null, null);
            }
            return super.onDoubleTap(e);
        }
    };
    private View.OnTouchListener tabTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.tab_news:
                    return tabGestureDetector.onTouchEvent(event);
                default:
                    break;
            }
            return false;
        }
    };

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
        tabGestureDetector = new GestureDetector(getContext(), tabGestureListener);
        tabNews.setOnTouchListener(tabTouchListener);
        currentFragment = addOrShowFragment(getTabFragment(rgMainTab.getCheckedRadioButtonId()), currentFragment, R.id.main_fl_content);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        currentFragment = addOrShowFragment(getTabFragment(checkedId), currentFragment, R.id.main_fl_content);
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

}
