package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.FilterDropEntity;
import com.icourt.alpha.fragment.dialogfragment.ProjectTypeSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.widget.popupwindow.TopMiddlePopup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/17
 * version 1.0.0
 */
public class TabProjectFragment extends BaseFragment implements TopMiddlePopup.OnItemClickListener, OnFragmentCallBackListener {
    private int select_position = 0;//选择的筛选选项
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    Unbinder unbinder;
    BaseFragmentAdapter baseFragmentAdapter;
    @BindView(R.id.titleAction2)
    ImageView titleAction2;
    TopMiddlePopup topMiddlePopup;
    MyProjectFragment myProjectFragment;
    List<Integer> selectedList = new ArrayList<>();

    List<FilterDropEntity> dropEntities = new ArrayList<>();
    FilterDropEntity doingEntity = new FilterDropEntity("进行中", "0", 2);//进行中
    FilterDropEntity doneEntity = new FilterDropEntity("已完结", "0", 4);//已完结
    FilterDropEntity pendingEntity = new FilterDropEntity("已搁置", "0", 7);//已搁置

    private Handler handler = new Handler();

    public static TabProjectFragment newInstance() {
        return new TabProjectFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_tab_find_project, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        select_position = 0;
        baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(baseFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
//        baseFragmentAdapter.bindTitle(true, Arrays.asList("全部", "我关注的"));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(myProjectFragment = MyProjectFragment.newInstance(MyProjectFragment.TYPE_ALL_PROJECT),
                        MyProjectFragment.newInstance(MyProjectFragment.TYPE_MY_ATTENTION_PROJECT)
//                        MyProjectFragment.newInstance(MyProjectFragment.TYPE_MY_PARTIC_PROJECT)
                ));
        topMiddlePopup = new TopMiddlePopup(getContext(), DensityUtil.getWidthInDp(getContext()), (int) (DensityUtil.getHeightInPx(getContext()) - DensityUtil.dip2px(getContext(), 75)), this);
        dropEntities.add(doingEntity);
        dropEntities.add(doneEntity);
        dropEntities.add(pendingEntity);
        getMatterStateCount(null);//默认获取所有类型的项目数量
        for (int i = 0; i < baseFragmentAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.task_unfinish_tab_custom_view);
            TextView titleTv = tab.getCustomView().findViewById(R.id.tab_custom_title_tv);
            ImageView downIv = tab.getCustomView().findViewById(R.id.tab_custom_title_iv);
            switch (i) {
                case 0:
                    titleTv.setTextColor(0xFF313131);
                    titleTv.setPadding(DensityUtil.dip2px(getContext(), 8), 0, 0, 0);
                    titleTv.setText("进行中");
                    downIv.setVisibility(View.VISIBLE);
                    tab.getCustomView().setOnClickListener(new OnTabClickListener());
                    break;
                case 1:
                    titleTv.setTextColor(0xFF979797);
                    titleTv.setText("我关注的");
                    downIv.setVisibility(View.GONE);
                    break;
            }
        }
        topMiddlePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setFirstTabImage(false);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                postDismissPop();
                titleAction.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                if (tabLayout.getTabAt(0).getCustomView() != null && tabLayout.getTabAt(1).getCustomView() != null) {
                    TextView titleTv_0 = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_custom_title_tv);
                    TextView titleTv_1 = tabLayout.getTabAt(1).getCustomView().findViewById(R.id.tab_custom_title_tv);
                    switch (position) {
                        case 0:
                            titleTv_0.setTextColor(0xFF313131);
                            titleTv_1.setTextColor(0xFF979797);
                            break;
                        case 1:
                            titleTv_0.setTextColor(0xFF979797);
                            titleTv_1.setTextColor(0xFF313131);
                            break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class OnTabClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (tabLayout.getTabAt(0) != null) {
                if (view.isSelected()) {
                    if (topMiddlePopup.isShowing()) {
                        postDismissPop();
                    } else {
                        topMiddlePopup.show(titleView, dropEntities, select_position);
                        setFirstTabImage(true);
                        getMatterStateCount(getMatterTypes());
                    }
                } else {
                    tabLayout.getTabAt(0).select();
                }
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //当Fragment不可见的时候，要隐藏弹出的PopWindow。
        if (hidden) {
            postDismissPop();
        }
    }

    /**
     * 隐藏pop
     */
    private void postDismissPop() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (topMiddlePopup != null) {
                    if (topMiddlePopup.isShowing()) {
                        topMiddlePopup.dismiss();
                    }
                }
            }
        }, 10);
    }

    /**
     * 获取选中的项目类型字符串
     *
     * @return
     */
    private String getMatterTypes() {
        if (selectedList != null && selectedList.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Integer integer : selectedList) {
                stringBuilder.append(String.valueOf(integer)).append(",");
            }
            return stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return null;
    }

    /**
     * 获取各个状态的任务数量
     */
    private void getMatterStateCount(String matterTypes) {
        callEnqueue(
                getApi().matterStateCountQuery(matterTypes),
                new SimpleCallBack<JsonElement>() {
                    @Override
                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                        JsonElement jsonElement = response.body().result;
                        if (jsonElement != null) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            if (jsonObject != null) {
                                doingEntity.count = jsonObject.get("openCount").getAsString();
                                doneEntity.count = jsonObject.get("closeCount").getAsString();
                                pendingEntity.count = jsonObject.get("terminationCount").getAsString();
                                dropEntities.clear();
                                dropEntities.add(doingEntity);
                                dropEntities.add(doneEntity);
                                dropEntities.add(pendingEntity);
                                if (topMiddlePopup != null && topMiddlePopup.isShowing()) {
                                    if (topMiddlePopup.getAdapter() != null) {
                                        topMiddlePopup.getAdapter().bindData(true, dropEntities);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                });
    }

    /**
     * 设置第一个tab的小图标
     *
     * @param isOpen
     */
    private void setFirstTabImage(boolean isOpen) {
        if (tabLayout != null)
            if (tabLayout.getTabAt(0) != null) {
                if (tabLayout.getTabAt(0).getCustomView() != null) {
                    ImageView iv = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_custom_title_iv);
                    iv.setImageResource(isOpen ? R.mipmap.task_dropup : R.mipmap.task_dropdown);
                }
            }

    }

    /**
     * 设置第一个tab的文本内容
     *
     * @param content
     */
    public void setFirstTabText(String content, int position) {
        if (tabLayout == null) return;
        if (tabLayout.getTabAt(0) == null) return;
        if (tabLayout.getTabAt(0).getCustomView() == null) return;
        TextView titleTv = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tab_custom_title_tv);
        titleTv.setText(content);
        select_position = position;
        topMiddlePopup.getAdapter().setSelectedPos(select_position);
    }

    @OnClick({R.id.titleAction, R.id.titleAction2})
    public void onViewClicked(View view) {
        postDismissPop();
        switch (view.getId()) {
            case R.id.titleAction:
                showProjectTypeSelectDialogFragment();
                break;
            case R.id.titleAction2:

                break;
        }
    }

    /**
     * 展示项目类型筛选框
     */
    private void showProjectTypeSelectDialogFragment() {
        String tag = ProjectTypeSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ProjectTypeSelectDialogFragment.newInstance(selectedList)
                .show(mFragTransaction, tag);

    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof ProjectTypeSelectDialogFragment) {
            if (params != null) {
                selectedList.clear();
                List<Integer> paramList = params.getIntegerArrayList(KEY_FRAGMENT_RESULT);
                if (paramList != null) {
                    selectedList.addAll(paramList);
                    if (paramList.size() > 0) {
                        titleAction.setImageResource(R.mipmap.project_filter);
                    } else {
                        titleAction.setImageResource(R.mipmap.project_unfilter);
                    }
                } else {
                    titleAction.setImageResource(R.mipmap.project_unfilter);
                }
                myProjectFragment.notifyFragmentUpdate(myProjectFragment, 101, params);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onItemClick(TopMiddlePopup topMiddlePopup, BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        topMiddlePopup.dismiss();
        FilterDropEntity filterDropEntity = (FilterDropEntity) adapter.getItem(position);
        setFirstTabText(filterDropEntity.name, position);
        Bundle bundle = new Bundle();
        bundle.putInt("status", filterDropEntity.stateType);
        myProjectFragment.notifyFragmentUpdate(myProjectFragment, 100, bundle);
    }
}
