package com.icourt.alpha.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.SFileLinkInfoEntity;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.SystemUtils;

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
 * date createTime：2017/8/15
 * version 2.1.0
 */
public class FileLinkFragment extends BaseFragment {

    protected static final String KEY_SEA_FILE_FROM_REPO_ID = "seaFileFromRepoId";//原仓库id
    protected static final String KEY_SEA_FILE_FROM_FILE_PATH = "seaFileFromFilePath";//原文件路径
    @BindView(R.id.file_access_pwd_tv)
    TextView fileAccessPwdTv;
    @BindView(R.id.file_access_time_limit_tv)
    TextView fileAccessTimeLimitTv;
    Unbinder unbinder;
    @BindView(R.id.file_share_link_tv)
    TextView fileShareLinkTv;
    @BindView(R.id.link_copy_tv)
    TextView linkCopyTv;
    SFileLinkInfoEntity sFileLinkInfoEntity;

    /**
     * @param fromRepoId
     * @param fromRepoFilePath
     * @return
     */
    public static FileLinkFragment newInstance(
            String fromRepoId,
            String fromRepoFilePath) {
        FileLinkFragment fragment = new FileLinkFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_FROM_FILE_PATH, fromRepoFilePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_file_link, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getApi().fileLinkQuery(
                getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, ""),
                getArguments().getString(KEY_SEA_FILE_FROM_FILE_PATH, ""),
                0).enqueue(new SFileCallBack<SFileLinkInfoEntity>() {
            @Override
            public void onSuccess(Call<SFileLinkInfoEntity> call, Response<SFileLinkInfoEntity> response) {
                sFileLinkInfoEntity = response.body();
                if (!isNoLink()) {
                    linkCopyTv.setVisibility(View.VISIBLE);
                    fileAccessPwdTv.setText(response.body().password);
                    fileAccessTimeLimitTv.setText(String.valueOf(response.body().expireTime));
                    fileShareLinkTv.setText(sFileLinkInfoEntity.getRealShareLink());
                } else {
                    linkCopyTv.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick({R.id.link_copy_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.link_copy_tv:
                copyLink();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void copyLink() {
        if (isNoLink()) {
            showTopSnackBar("暂无链接可复制");
            return;
        }
        if (!TextUtils.isEmpty(sFileLinkInfoEntity.password)) {
            SystemUtils.copyToClipboard(getContext(), "link", String.format("链接:%s\n密码:%s", sFileLinkInfoEntity.getRealShareLink(), sFileLinkInfoEntity.password));
        } else {
            SystemUtils.copyToClipboard(getContext(), "link", String.format("链接:%s", sFileLinkInfoEntity.getRealShareLink()));
        }
        showToast("已复制到剪切版");
    }

    /**
     * 是否无链接
     *
     * @return
     */
    private boolean isNoLink() {
        return sFileLinkInfoEntity == null || sFileLinkInfoEntity.isNoLink();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
