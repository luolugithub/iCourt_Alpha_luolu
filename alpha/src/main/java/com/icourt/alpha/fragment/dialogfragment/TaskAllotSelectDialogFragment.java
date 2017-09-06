package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskOwerListAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.TaskEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.ClearEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  选择任务分配人
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/16
 * version 2.0.0
 */

public class TaskAllotSelectDialogFragment extends BaseDialogFragment implements BaseRecyclerAdapter.OnItemClickListener {

    Unbinder unbinder;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    String projectId;
    HeaderFooterAdapter<TaskOwerListAdapter> headerFooterAdapter;
    TaskOwerListAdapter taskOwerListAdapter;
    final ArrayList<TaskEntity.TaskItemEntity.AttendeeUserEntity> attendeeUserEntities = new ArrayList<>();
    OnFragmentCallBackListener onFragmentCallBackListener;
    @BindView(R.id.header_comm_search_input_et)
    ClearEditText headerCommSearchInputEt;
    @BindView(R.id.header_comm_search_cancel_tv)
    TextView headerCommSearchCancelTv;
    @BindView(R.id.header_comm_search_input_ll)
    LinearLayout headerCommSearchInputLl;
    List<TaskEntity.TaskItemEntity.AttendeeUserEntity> searchEntites = new ArrayList<>();//搜索结果集
    List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attendeeUserEntitys = new ArrayList<>();

    public static TaskAllotSelectDialogFragment newInstance(@NonNull String projectId, List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attendeeUserEntities) {
        TaskAllotSelectDialogFragment taskAllotSelectDialogFragment = new TaskAllotSelectDialogFragment();
        Bundle args = new Bundle();
        args.putString("projectId", projectId);
        args.putSerializable("list", (Serializable) attendeeUserEntities);
        taskAllotSelectDialogFragment.setArguments(args);
        return taskAllotSelectDialogFragment;
    }

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
        View view = super.onCreateView(R.layout.dialog_fragment_task_select_layout, inflater, container, savedInstanceState);
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
        projectId = getArguments().getString("projectId");
        attendeeUserEntities.clear();
        ArrayList<TaskEntity.TaskItemEntity.AttendeeUserEntity> list = (ArrayList<TaskEntity.TaskItemEntity.AttendeeUserEntity>)getArguments().getSerializable("list");
        if(list!=null)
        attendeeUserEntities.addAll(list);

        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.setHasFixedSize(true);
        headerFooterAdapter = new HeaderFooterAdapter<>(taskOwerListAdapter = new TaskOwerListAdapter());
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerview);
        headerFooterAdapter.addHeader(headerView);
        recyclerview.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        registerClick(headerView.findViewById(R.id.header_comm_search_ll));
        recyclerview.setAdapter(headerFooterAdapter);

        taskOwerListAdapter.setOnItemClickListener(this);

        headerCommSearchInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    getData(true);
                } else {
                    searchOwerByName(s.toString());
                }
            }
        });
        headerCommSearchInputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), headerCommSearchInputEt);
                        if (!TextUtils.isEmpty(headerCommSearchInputEt.getText())) {
                            searchOwerByName(headerCommSearchInputEt.getText().toString());
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });
        headerCommSearchInputLl.setVisibility(View.GONE);

        getData(true);
    }

    @OnClick({R.id.bt_ok,
            R.id.bt_cancel,
            R.id.header_comm_search_cancel_tv})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt_ok:
                if (getParentFragment() instanceof OnFragmentCallBackListener) {
                    onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
                }
                if (onFragmentCallBackListener != null) {
                    List<TaskEntity.TaskItemEntity.AttendeeUserEntity> attusers = new ArrayList<>();
                    attusers.addAll(attendeeUserEntities);
                    Bundle bunble = new Bundle();
                    bunble.putSerializable("list", (Serializable) attusers);
                    onFragmentCallBackListener.onFragmentCallBack(TaskAllotSelectDialogFragment.this, 0, bunble);
                }
                dismiss();
                break;
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.header_comm_search_cancel_tv:
                headerCommSearchInputEt.setText("");
                SystemUtils.hideSoftKeyBoard(getActivity(), headerCommSearchInputEt, true);
                headerCommSearchInputLl.setVisibility(View.GONE);
                break;
            case R.id.header_comm_search_ll:
                headerCommSearchInputLl.setVisibility(View.VISIBLE);
                SystemUtils.showSoftKeyBoard(getActivity(), headerCommSearchInputEt);
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        taskOwerListAdapter.clearData();
        taskOwerListAdapter.getSelectedArray().clear();
        getApi().taskOwerListQuery(projectId, "").enqueue(new SimpleCallBack<List<TaskEntity.TaskItemEntity.AttendeeUserEntity>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<TaskEntity.TaskItemEntity.AttendeeUserEntity>>> call, Response<ResEntity<List<TaskEntity.TaskItemEntity.AttendeeUserEntity>>> response) {
                if (response.body().result != null) {
                    taskOwerListAdapter.bindData(true, response.body().result);
                    attendeeUserEntitys = response.body().result;
                    if (attendeeUserEntities != null) {
                        if (attendeeUserEntities.size() > 0) {
                            for (int i = 0; i < response.body().result.size(); i++) {
                                for (TaskEntity.TaskItemEntity.AttendeeUserEntity attendeeUserEntity : attendeeUserEntities) {
                                    if (attendeeUserEntity == null) continue;
                                    if (TextUtils.equals(attendeeUserEntity.userId, response.body().result.get(i).userId)) {
                                        taskOwerListAdapter.setSelected(i, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 搜索负责人
     *
     * @param name
     */
    private void searchOwerByName(String name) {
        if (TextUtils.isEmpty(name)) return;
        if (taskOwerListAdapter == null) return;
        if (attendeeUserEntitys == null) return;
        if (attendeeUserEntitys.size() <= 0) return;

        searchEntites.clear();
        for (TaskEntity.TaskItemEntity.AttendeeUserEntity taskOwerEntity : attendeeUserEntitys) {
            if (taskOwerEntity.userName.contains(name)) {
                searchEntites.add(taskOwerEntity);
            }
        }
        if (searchEntites != null) {
            taskOwerListAdapter.clearSelected();
            taskOwerListAdapter.bindData(true, searchEntites);
            if (attendeeUserEntities != null) {
                for (int i = 0; i < searchEntites.size(); i++) {
                    for (TaskEntity.TaskItemEntity.AttendeeUserEntity taskOwerEntity : attendeeUserEntities) {
                        if (TextUtils.equals(taskOwerEntity.userId, searchEntites.get(i).userId)) {
                            taskOwerListAdapter.setSelected(i, true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        TaskEntity.TaskItemEntity.AttendeeUserEntity entity = taskOwerListAdapter.getItem(taskOwerListAdapter.getRealPos(position));
        taskOwerListAdapter.toggleSelected(position);
        if (taskOwerListAdapter.isSelected(taskOwerListAdapter.getRealPos(position))) {
            if (!attendeeUserEntities.contains(entity))
                attendeeUserEntities.add(entity);
        } else {
            if (attendeeUserEntities.contains(entity))
                attendeeUserEntities.remove(entity);
        }
    }
}
