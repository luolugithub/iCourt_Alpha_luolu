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
import com.icourt.alpha.adapter.TaskMemberAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.TaskMemberWrapEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;

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
 * date createTime：2017/5/19
 * version 1.0.0
 */
public class TaskMemberSelectDialogFragment extends BaseDialogFragment {

    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    Unbinder unbinder;
    TaskMemberAdapter taskMemberAdapter;

    public static TaskMemberSelectDialogFragment newInstance() {
        TaskMemberSelectDialogFragment contactSelectDialogFragment = new TaskMemberSelectDialogFragment();
        Bundle args = new Bundle();
        contactSelectDialogFragment.setArguments(args);
        return contactSelectDialogFragment;
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
        View view = super.onCreateView(R.layout.dialog_fragment_contact_select, inflater, container, savedInstanceState);
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(taskMemberAdapter = new TaskMemberAdapter(true));
        taskMemberAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                taskMemberAdapter.setSelectedPos(position);
            }
        });
        getData(true);
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        getApi().getTaskMembers()
                .enqueue(new SimpleCallBack<List<TaskMemberWrapEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<TaskMemberWrapEntity>>> call, Response<ResEntity<List<TaskMemberWrapEntity>>> response) {
                        if (response.body().result != null && !response.body().result.isEmpty()) {
                            TaskMemberWrapEntity taskMemberWrapEntity = response.body().result.get(0);
                            if (taskMemberWrapEntity != null) {
                                taskMemberAdapter.bindData(isRefresh, taskMemberWrapEntity.members);
                            }
                        }
                    }
                });
    }


    @OnClick({R.id.bt_cancel, R.id.bt_ok})
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
                    Bundle params = new Bundle();
                    params.putSerializable(KEY_FRAGMENT_RESULT, taskMemberAdapter.getItem(taskMemberAdapter.getSelectedPos()));
                    onFragmentCallBackListener.onFragmentCallBack(this, 0, params);
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
}
