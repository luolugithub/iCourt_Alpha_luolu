package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectTypeListAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.ProjectTypeEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  zhaolu  E-mail:zhaolu@icourt.cc
 * date createTime：2017/8/8
 * version 2.0.0
 */
public class ProjectTypeSelectDialogFragment extends BaseDialogFragment implements BaseRecyclerAdapter.OnItemClickListener {

    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    @BindView(R.id.bt_reset)
    TextView btReset;
    ProjectTypeListAdapter projectTypeListAdapter;
    List<Integer> selectedPosition = new ArrayList<>();

    public static ProjectTypeSelectDialogFragment newInstance(List<Integer> selectedPosition) {
        ProjectTypeSelectDialogFragment workTypeSelectDialogFragment = new ProjectTypeSelectDialogFragment();
        Bundle args = new Bundle();
        args.putIntegerArrayList("selectedPosition", (ArrayList<Integer>) selectedPosition);
        workTypeSelectDialogFragment.setArguments(args);
        return workTypeSelectDialogFragment;
    }

    OnFragmentCallBackListener onFragmentCallBackListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_project_type, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setWindowAnimations(R.style.AppThemeSlideAnimation);
                window.setGravity(Gravity.BOTTOM);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, dp20, dp20 / 2, dp20);
                }
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(projectTypeListAdapter = new ProjectTypeListAdapter());
        projectTypeListAdapter.setOnItemClickListener(this);
        getData(true);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        List<ProjectTypeEntity> projectTypes = new ArrayList<>();
        projectTypes.add(new ProjectTypeEntity("争议解决", 0, R.mipmap.project_type_dis));
        projectTypes.add(new ProjectTypeEntity("非诉专项", 1, R.mipmap.project_type_noju));
        projectTypes.add(new ProjectTypeEntity("常年顾问", 2, R.mipmap.project_type_coun));
        projectTypes.add(new ProjectTypeEntity("内部事务", 3, R.mipmap.project_type_aff));
        projectTypeListAdapter.bindData(true, projectTypes);
        setLastSelected();
    }

    private void setLastSelected() {
        selectedPosition = getArguments().getIntegerArrayList("selectedPosition");
        if (selectedPosition != null && selectedPosition.size() > 0) {
            for (int i = 0; i < selectedPosition.size(); i++) {
                projectTypeListAdapter.toggleSelected(selectedPosition.get(i));
            }
            btReset.setVisibility(View.VISIBLE);
        } else {
            btReset.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick({R.id.bt_cancel,
            R.id.bt_ok,
            R.id.bt_reset})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.bt_ok:
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                if (onFragmentCallBackListener != null) {
                    Bundle bundle = new Bundle();
                    bundle.putIntegerArrayList(KEY_FRAGMENT_RESULT, (ArrayList<Integer>) projectTypeListAdapter.getSelectedPositions());
                    onFragmentCallBackListener.onFragmentCallBack(ProjectTypeSelectDialogFragment.this, 0, bundle);
                }
                dismiss();
                break;
            case R.id.bt_reset:
                projectTypeListAdapter.clearSelected();
                btReset.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showResetBtn() {
        if (projectTypeListAdapter.getSelectedData() != null && projectTypeListAdapter.getSelectedData().size() > 0) {
            btReset.setVisibility(View.VISIBLE);
        } else {
            btReset.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        projectTypeListAdapter.toggleSelected(position);
        showResetBtn();
    }
}
