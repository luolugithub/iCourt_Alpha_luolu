package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.RepoTypeAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.HeaderFooterAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.RepoTypeEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.view.smartrefreshlayout.EmptyRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Description  资料库类型列表
 * Company Beijing icourt
 * author  youxuan  l:xuanyouwu@163.com
 * date createTime：2017/9/9
 * version 2.1.0
 */
public class RepoNavigationFragment extends BaseFragment
        implements BaseRecyclerAdapter.OnItemClickListener {

    private static final String KEY_FOOTER_NOTICE = "footerNotice";
    Unbinder unbinder;
    RepoTypeAdapter repoTypeAdapter;
    HeaderFooterAdapter<RepoTypeAdapter> headerFooterAdapter;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    TextView footerView;
    OnFragmentCallBackListener onFragmentCallBackListener;
    String footerNotice = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentCallBackListener) {
            onFragmentCallBackListener = (OnFragmentCallBackListener) getParentFragment();
        } else {
            try {
                onFragmentCallBackListener = (OnFragmentCallBackListener) context;
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    public static RepoNavigationFragment newInstance(String footerNotice) {
        RepoNavigationFragment fragment = new RepoNavigationFragment();
        Bundle args = new Bundle();
        args.putString(KEY_FOOTER_NOTICE, footerNotice);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_repo_navigation, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        footerNotice = getArguments().getString(KEY_FOOTER_NOTICE, "");
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(ItemDecorationUtils.getCommMagin10Divider(getContext(), true));
        headerFooterAdapter = new HeaderFooterAdapter<>(repoTypeAdapter = new RepoTypeAdapter());

        footerView = (TextView) HeaderFooterAdapter.inflaterView(getContext(), R.layout.footer_folder_document_num, recyclerView.getRecyclerView());
        headerFooterAdapter.addFooter(footerView);
        footerView.setText("将文件保存到可读写权限的资料库");
        footerView.setText(footerNotice);

        recyclerView.setAdapter(headerFooterAdapter);
        String[] repoArray = getResources().getStringArray(R.array.repo_type_details_array);
        repoTypeAdapter.bindData(
                true,
                Arrays.asList(new RepoTypeEntity(SFileConfig.REPO_MINE, repoArray[0]),
                        new RepoTypeEntity(SFileConfig.REPO_SHARED_ME, repoArray[1]),
                        new RepoTypeEntity(SFileConfig.REPO_LAWFIRM, repoArray[2]),
                        new RepoTypeEntity(SFileConfig.REPO_PROJECT, repoArray[3]))
        );
        repoTypeAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        RepoTypeEntity item = repoTypeAdapter.getItem(position);
        if (item == null) return;
        if (onFragmentCallBackListener != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_FRAGMENT_RESULT, item);
            onFragmentCallBackListener.onFragmentCallBack(this, 1, bundle);
        }
    }
}
