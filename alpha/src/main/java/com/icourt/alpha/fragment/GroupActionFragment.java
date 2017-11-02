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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.GroupAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.GroupEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.GroupAndContactUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.view.ClearEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * date createTime：2017/5/10
 * version 1.0.0
 */
public class GroupActionFragment extends BaseFragment {
    Unbinder unbinder;
    GroupAdapter groupAdapter;
    HeaderFooterAdapter<GroupAdapter> headerFooterAdapter;
    private final List<GroupEntity> groupEntities = new ArrayList<>();
    private final Map<String, String> groupEntitiesPinyinMap = new HashMap<>();
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.header_comm_search_input_et)
    ClearEditText headerCommSearchInputEt;
    @BindView(R.id.header_comm_search_cancel_tv)
    TextView headerCommSearchCancelTv;
    @BindView(R.id.header_comm_search_input_ll)
    LinearLayout headerCommSearchInputLl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_group_action, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static GroupActionFragment newInstance() {
        return new GroupActionFragment();
    }

    @Override
    protected void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        headerFooterAdapter = new HeaderFooterAdapter<>(groupAdapter = new GroupAdapter());
        View headerView = HeaderFooterAdapter.inflaterView(getContext(), R.layout.header_search_comm, recyclerView);
        headerFooterAdapter.addHeader(headerView);
        registerClick(headerView.findViewById(R.id.header_comm_search_ll));
        recyclerView.setAdapter(headerFooterAdapter);
        groupAdapter.setSelectable(true);
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
                    groupAdapter.bindData(true, groupEntities);
                } else {
                    queryByName(s.toString());
                }
            }
        });
        headerCommSearchInputLl.setVisibility(View.GONE);
        getData(true);
    }

    @OnClick({R.id.header_comm_search_cancel_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_comm_search_ll:
                headerCommSearchInputLl.setVisibility(View.VISIBLE);
                SystemUtils.showSoftKeyBoard(getActivity(), headerCommSearchInputEt);
                break;
            case R.id.header_comm_search_cancel_tv:
                headerCommSearchInputEt.setText("");
                SystemUtils.hideSoftKeyBoard(getActivity(), headerCommSearchInputEt, true);
                headerCommSearchInputLl.setVisibility(View.GONE);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
//        getChatApi().groupsQuery(0, true)  //获取所有讨论组
        callEnqueue(
                getChatApi().groupsQueryJoind(0, true),
                new SimpleCallBack<List<GroupEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<GroupEntity>>> call, Response<ResEntity<List<GroupEntity>>> response) {
                        groupEntities.clear();
                        groupEntities.addAll(response.body().result);
                        groupAdapter.bindData(true, groupEntities);
                    }
                });
    }

    protected void queryByName(String name) {
        makeSurePinyinDetected();

        List<GroupEntity> filterGroupEntities = new ArrayList<>();
        for (GroupEntity groupEntity : groupEntities) {
            if (groupEntity != null && !TextUtils.isEmpty(groupEntity.name)) {
                if (groupEntity.name.contains(name) || StringUtils.containsIgnoreCase(groupEntitiesPinyinMap.get(groupEntity.id), name)) {
                    filterGroupEntities.add(groupEntity);
                }
            }
        }
        groupAdapter.bindData(true, filterGroupEntities);
    }

    private void makeSurePinyinDetected() {
        if (groupEntities.size() > 0 && groupEntitiesPinyinMap.isEmpty()) {
            for (GroupEntity groupEntity : groupEntities) {
                if (groupEntity == null) {
                    continue;
                }
                String pinyin = GroupAndContactUtils.makePinyin(getContext(), groupEntity.name, "");
                groupEntitiesPinyinMap.put(groupEntity.id, pinyin);
            }
        }
    }

    @Override
    public Bundle getFragmentData(int type, Bundle inBundle) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_FRAGMENT_RESULT, groupAdapter.getSelectedData());
        return bundle;
    }

}
