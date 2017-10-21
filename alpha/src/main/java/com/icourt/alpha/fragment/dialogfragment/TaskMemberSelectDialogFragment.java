package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.TaskMemberAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.TaskMemberEntity;
import com.icourt.alpha.entity.bean.TaskMemberWrapEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.SystemUtils;

import java.util.ArrayList;
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
    HeaderFooterAdapter<TaskMemberAdapter> headerFooterAdapter;
    @BindView(R.id.header_comm_search_input_et)
    EditText headerCommSearchInputEt;
    @BindView(R.id.header_comm_search_cancel_tv)
    TextView headerCommSearchCancelTv;
    @BindView(R.id.header_comm_search_input_ll)
    LinearLayout headerCommSearchInputLl;
    List<TaskMemberEntity> members = new ArrayList<TaskMemberEntity>();
    @BindView(R.id.share_permission_rw_rb)
    RadioButton sharePermissionRwRb;
    @BindView(R.id.share_permission_r_rb)
    RadioButton sharePermissionRRb;
    @BindView(R.id.title_share_permission)
    LinearLayout titleSharePermission;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;

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
        contentEmptyText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.icon_placeholder_user, 0, 0);
        contentEmptyText.setText(R.string.empty_list_im_search_group_member);
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        headerFooterAdapter = new HeaderFooterAdapter<>(taskMemberAdapter = new TaskMemberAdapter(true));
        taskMemberAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                if (contentEmptyText != null) {
                    contentEmptyText.setVisibility(taskMemberAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                }
            }
        });
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        headerFooterAdapter.addHeader(headerView);
        registerClick(headerView.findViewById(R.id.header_comm_search_ll));
        recyclerView.setAdapter(headerFooterAdapter);

        taskMemberAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                taskMemberAdapter.setSelectedPos(adapter.getRealPos(position));
            }
        });
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
                    taskMemberAdapter.clearSelected();
                    getData(true);
                } else {
                    searchUserByName(s.toString());
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
                            searchUserByName(headerCommSearchInputEt.getText().toString());
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

    /**
     * 搜索
     *
     * @param name
     */
    private void searchUserByName(String name) {
        if (TextUtils.isEmpty(name)) {return;}
        if (members != null) {
            List<TaskMemberEntity> memberEntities = new ArrayList<TaskMemberEntity>();
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i).userName.contains(name)) {
                    memberEntities.add(members.get(i));
                }
            }
            taskMemberAdapter.clearSelected();
            taskMemberAdapter.bindData(true, memberEntities);
        }
    }

    @Override
    protected void getData(final boolean isRefresh) {
        super.getData(isRefresh);
        //有权限
        callEnqueue(
                getApi().getPremissionTaskMembers(),
                new SimpleCallBack<List<TaskMemberWrapEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<TaskMemberWrapEntity>>> call, Response<ResEntity<List<TaskMemberWrapEntity>>> response) {
                        if (response.body().result != null && !response.body().result.isEmpty()) {
                            for (TaskMemberWrapEntity taskMemberWrapEntity : response.body().result) {
                                if (taskMemberWrapEntity.members != null) {
                                    members.addAll(taskMemberWrapEntity.members);
                                }
                            }
                            taskMemberAdapter.bindData(isRefresh, members);
                        }
                    }
                });
    }


    @OnClick({R.id.bt_cancel, R.id.bt_ok, R.id.header_comm_search_cancel_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.header_comm_search_ll:
                headerCommSearchInputLl.setVisibility(View.VISIBLE);
                SystemUtils.showSoftKeyBoard(getActivity(), headerCommSearchInputEt);
                break;
            case R.id.header_comm_search_cancel_tv:
                headerCommSearchInputEt.setText("");
                SystemUtils.hideSoftKeyBoard(getActivity(), headerCommSearchInputEt, true);
                headerCommSearchInputLl.setVisibility(View.GONE);
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
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
