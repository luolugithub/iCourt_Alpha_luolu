package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.GroupAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/10
 * version 1.0.0
 */
public class GroupActionFragment extends BaseFragment {
    Unbinder unbinder;
    GroupAdapter groupAdapter;
    @BindView(R.id.header_input_et)
    EditText headerInputEt;
    @BindView(R.id.rl_comm_search)
    RelativeLayout rlCommSearch;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private final List<GroupEntity> groupEntities = new ArrayList<>();

    public static GroupActionFragment newInstance() {
        return new GroupActionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_group_action, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(groupAdapter = new GroupAdapter());
        groupAdapter.setSelectable(true);
        headerInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    groupAdapter.bindData(true, groupEntities);
                } else {
                    queryByName(s.toString());
                }
            }
        });
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getChatApi().groupsQuery(0, true)
                .enqueue(new SimpleCallBack<List<GroupEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupEntity>>> call, Response<ResEntity<List<GroupEntity>>> response) {
                        groupEntities.clear();
                        groupEntities.addAll(response.body().result);
                        groupAdapter.bindData(true, groupEntities);
                    }
                });
    }

    protected void queryByName(String name) {
        List<GroupEntity> filterGroupEntities = new ArrayList<>();
        for (GroupEntity groupEntity : groupEntities) {
            if (groupEntity != null && !TextUtils.isEmpty(groupEntity.name)) {
                if (groupEntity.name.contains(name)) {
                    filterGroupEntities.add(groupEntity);
                }
            }
        }
        groupAdapter.bindData(true, filterGroupEntities);
    }

    @Override
    public Bundle getFragmentData(int type, Bundle inBundle) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_FRAGMENT_RESULT, groupAdapter.getSelectedData());
        return bundle;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
