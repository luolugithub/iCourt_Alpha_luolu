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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.ProjectAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.SystemUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  项目选择对话框
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/11
 * version 1.0.0
 */
public class ProjectSimpleSelectDialogFragment extends BaseDialogFragment implements BaseRecyclerAdapter.OnItemClickListener {

    Unbinder unbinder;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.bt_cancel)
    TextView btCancel;
    @BindView(R.id.bt_ok)
    TextView btOk;
    ProjectAdapter projectAdapter;
    @BindView(R.id.header_input_et)
    EditText headerInputEt;
    @BindView(R.id.rl_comm_search)
    RelativeLayout rlCommSearch;

    public static ProjectSimpleSelectDialogFragment newInstance(@Nullable String selectedProjectId) {
        ProjectSimpleSelectDialogFragment projectSimpleSelectDialogFragment = new ProjectSimpleSelectDialogFragment();
        Bundle args = new Bundle();
        args.putString("selectedProjectId", selectedProjectId);
        projectSimpleSelectDialogFragment.setArguments(args);
        return projectSimpleSelectDialogFragment;
    }

    OnFragmentCallBackListener onFragmentCallBackListener;

    private String selectedProjectId;

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
        View view = super.onCreateView(R.layout.dialog_fragment_simple_project_select, inflater, container, savedInstanceState);
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
        recyclerView.setAdapter(projectAdapter = new ProjectAdapter(true));
        projectAdapter.setOnItemClickListener(this);
        selectedProjectId = getArguments().getString("selectedProjectId");

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
                    getData(true);
                } else {
                    searchProjectByName(s.toString());
                }
            }
        });
        headerInputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), headerInputEt);
                        if (!TextUtils.isEmpty(headerInputEt.getText())) {
                            searchProjectByName(headerInputEt.getText().toString());
                        }
                    }
                    return true;
                    default:
                        return false;
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING: {
                        SystemUtils.hideSoftKeyBoard(getActivity(), headerInputEt, true);
                    }
                    break;
                }
            }

        });
        showLoadingDialog(null);
        getData(true);
    }

    /**
     * 按名称搜索项目
     *
     * @param projectName
     */
    private void searchProjectByName(final String projectName) {
        if (TextUtils.isEmpty(projectName)) return;
        getApi().timingProjectQuery(1, "0,2,7", projectName)
                .enqueue(new SimpleCallBack<List<ProjectEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<ProjectEntity>>> call, Response<ResEntity<List<ProjectEntity>>> response) {
                        projectAdapter.clearData();
                        projectAdapter.bindData(true, response.body().result);
                        setSelectedProject();
                    }
                });
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        /**
         * 默认获取的关注的非完结的项目
         */
        getApi().timingProjectQuery(1, "0,2,7")
                .enqueue(new SimpleCallBack<List<ProjectEntity>>() {
                    @Override
                    public void onSuccess(Call<ResEntity<List<ProjectEntity>>> call, Response<ResEntity<List<ProjectEntity>>> response) {
                        dismissLoadingDialog();
                        projectAdapter.bindData(true, response.body().result);
                        setSelectedProject();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<List<ProjectEntity>>> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 设置选中的item
     */
    private void setSelectedProject() {
        List<ProjectEntity> data = projectAdapter.getData();
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.pkId = selectedProjectId;
        int indexOf = data.indexOf(projectEntity);
        if (indexOf >= 0) {
            projectAdapter.setSelectedPos(indexOf);
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
                    Bundle params = new Bundle();
                    params.putSerializable(KEY_FRAGMENT_RESULT, projectAdapter.getItem(projectAdapter.getSelectedPos()));
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

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        projectAdapter.setSelectedPos(position);
        ProjectEntity item = projectAdapter.getItem(position);
        if (item != null) {
            selectedProjectId = item.pkId;
        }
    }
}
