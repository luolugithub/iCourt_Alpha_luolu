package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.entity.bean.TaskGroupEntity;
import com.icourt.alpha.fragment.ProjectListFragment;
import com.icourt.alpha.fragment.TaskGroupListFragment;
import com.icourt.alpha.interfaces.INotifyFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.view.NoScrollViewPager;

import java.io.Serializable;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description  项目选择对话框
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/11
 * version 1.0.0
 */
public class ProjectSelectDialogFragment extends BaseDialogFragment
        implements OnFragmentCallBackListener {


    @BindView(R.id.titleBack)
    TextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.viewPager)
    NoScrollViewPager viewPager;
    Unbinder unbinder;

    BaseFragmentAdapter baseFragmentAdapter;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    private String projectId;

    public static ProjectSelectDialogFragment newInstance() {
        return new ProjectSelectDialogFragment();
    }

    OnProjectTaskGroupSelectListener onProjectTaskGroupSelectListener;

    public interface OnProjectTaskGroupSelectListener {
        void onProjectTaskGroupSelect(String projectId, String taskGroupId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onProjectTaskGroupSelectListener = (OnProjectTaskGroupSelectListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_project_select, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.BOTTOM);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, dp20, dp20 / 2, dp20);
                }
            }
        }
        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));
        titleContent.setText("选择项目");
        titleBack.setVisibility(View.INVISIBLE);
        btOk.setVisibility(View.INVISIBLE);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    titleContent.setText("选择项目");
                    titleBack.setVisibility(View.INVISIBLE);
                    btOk.setVisibility(View.INVISIBLE);
                } else {
                    titleContent.setText("选择任务组");
                    titleBack.setVisibility(View.VISIBLE);
                    btOk.setVisibility(View.VISIBLE);
                }
            }
        });
        baseFragmentAdapter.bindData(true, Arrays.asList(ProjectListFragment.newInstance(),
                TaskGroupListFragment.newInstance(null)));

    }

    @OnClick({R.id.titleBack, R.id.bt_cancel, R.id.bt_ok})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                viewPager.setCurrentItem(0);
                break;
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.bt_ok:
                TaskGroupListFragment fragment = (TaskGroupListFragment) baseFragmentAdapter.getItem(1);
                Bundle fragmentData = fragment.getFragmentData(0, null);
                if (fragmentData == null) return;
                Serializable serializable = fragmentData.getSerializable(KEY_FRAGMENT_RESULT);
                String groupTaskId = null;
                if (serializable instanceof TaskGroupEntity) {
                    groupTaskId = ((TaskGroupEntity) serializable).id;
                }
                if (onProjectTaskGroupSelectListener != null) {
                    onProjectTaskGroupSelectListener.onProjectTaskGroupSelect(projectId, groupTaskId);
                }
                dismiss();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment == baseFragmentAdapter.getItem(0) && params != null) {
            Fragment item0 = baseFragmentAdapter.getItem(0);
            Fragment item1 = baseFragmentAdapter.getItem(1);
            if (item0 instanceof INotifyFragment) {
                Serializable serializable = params.getSerializable(KEY_FRAGMENT_RESULT);
                if (serializable instanceof ProjectEntity) {
                    if (item1 instanceof INotifyFragment) {
                        Bundle bundle = new Bundle();
                        projectId = ((ProjectEntity) serializable).pkId;
                        bundle.putString("projectId", projectId);
                        ((INotifyFragment) item1).notifyFragmentUpdate(item1, 0, bundle);
                    }
                }
            }
            viewPager.setCurrentItem(1);
        }
    }
}
