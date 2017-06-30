package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.WorkTypeAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.WorkType;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.ItemDecorationUtils;

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
 * date createTime：2017/5/16
 * version 1.0.0
 */
public class WorkTypeSelectDialogFragment extends BaseDialogFragment implements BaseRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    Unbinder unbinder;
    WorkTypeAdapter workTypeAdapter;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;

    public static WorkTypeSelectDialogFragment newInstance(@NonNull String projectId, String selectedWorkType) {
        WorkTypeSelectDialogFragment workTypeSelectDialogFragment = new WorkTypeSelectDialogFragment();
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        args.putString("selectedWorkType", selectedWorkType);
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
        View view = super.onCreateView(R.layout.dialog_fragment_work_type, inflater, container, savedInstanceState);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommFullDivider(getContext(), true));
        recyclerView.setAdapter(workTypeAdapter = new WorkTypeAdapter(true));
        workTypeAdapter.setOnItemClickListener(this);
        getData(true);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getApi().queryWorkTypes(getArguments().getString("projectId"))
                .enqueue(new SimpleCallBack<List<WorkType>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<WorkType>>> call, Response<ResEntity<List<WorkType>>> response) {
                        workTypeAdapter.bindData(isRefresh, response.body().result);
                        setLastSelected();
                    }
                });
    }

    private void setLastSelected() {
        String selectedWorkType = getArguments().getString("selectedWorkType", null);
        if (!TextUtils.isEmpty(selectedWorkType)) {
            WorkType targetWorkType = new WorkType();
            targetWorkType.pkId = selectedWorkType;
            int indexOf = workTypeAdapter.getData().indexOf(targetWorkType);
            if (indexOf >= 0) {
                workTypeAdapter.setSelectedPos(indexOf);
            }
        }
    }

    @OnClick({R.id.bt_cancel,
            R.id.bt_ok})
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
                    bundle.putSerializable(KEY_FRAGMENT_RESULT, workTypeAdapter.getItem(workTypeAdapter.getSelectedPos()));
                    onFragmentCallBackListener.onFragmentCallBack(WorkTypeSelectDialogFragment.this, 0, bundle);
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        workTypeAdapter.setSelectedPos(position);
    }
}
