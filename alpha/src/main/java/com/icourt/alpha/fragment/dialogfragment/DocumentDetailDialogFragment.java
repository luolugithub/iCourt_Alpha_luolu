package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.FileVersionCommits;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.fragment.FileChangeHistoryFragment;
import com.icourt.alpha.fragment.FileInnerShareFragment;
import com.icourt.alpha.fragment.FileLinkFragment;
import com.icourt.alpha.fragment.FileVersionListFragment;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.FileUtils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description   文件详情
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/9
 * version 2.1.0
 */
public class DocumentDetailDialogFragment extends BaseDialogFragment {
    protected static final String KEY_SEA_FILE_FROM_REPO_ID = "seaFileFromRepoId";//原仓库id
    protected static final String KEY_SEA_FILE_FROM_FILE_PATH = "seaFileFromFilePath";//原文件路径

    Unbinder unbinder;
    BaseFragmentAdapter baseFragmentAdapter;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.file_type_iv)
    ImageView fileTypeIv;
    @BindView(R.id.file_version_tv)
    TextView fileVersionTv;
    @BindView(R.id.file_title_tv)
    TextView fileTitleTv;
    @BindView(R.id.file_size_tv)
    TextView fileSizeTv;
    @BindView(R.id.file_create_info_tv)
    TextView fileCreateInfoTv;
    @BindView(R.id.file_update_info_tv)
    TextView fileUpdateInfoTv;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    FolderDocumentEntity folderDocumentEntity;
    String fromRepoId, fromRepoFilePath;

    public static void show(@NonNull String fromRepoId,
                            String fromRepoFilePath,
                            FolderDocumentEntity folderDocumentEntity,
                            @NonNull FragmentManager fragmentManager) {
        if (folderDocumentEntity == null) return;
        if (fragmentManager == null) return;
        String tag = DocumentDetailDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        show(newInstance(fromRepoId, fromRepoFilePath, folderDocumentEntity), tag, mFragTransaction);
    }


    public static DocumentDetailDialogFragment newInstance(
            String fromRepoId,
            String fromRepoFilePath,
            FolderDocumentEntity folderDocumentEntity) {
        DocumentDetailDialogFragment fragment = new DocumentDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_FROM_FILE_PATH, fromRepoFilePath);
        args.putSerializable("data", folderDocumentEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_folder_document_detail, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.windowAnimations = R.style.SlideAnimBottom;
                window.setAttributes(attributes);
            }
        }
        fromRepoId = getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, "");
        fromRepoFilePath = getArguments().getString(KEY_SEA_FILE_FROM_FILE_PATH, "");
        folderDocumentEntity = (FolderDocumentEntity) getArguments().getSerializable("data");
        if (folderDocumentEntity == null) return;
        fileTitleTv.setText(folderDocumentEntity.name);
        fileSizeTv.setText(FileUtils.bFormat(folderDocumentEntity.size));
        fileTypeIv.setImageResource(getFileIcon(folderDocumentEntity.name));
        if (folderDocumentEntity.isDir()) {
            titleContent.setText("文件夹详情");
        } else {
            titleContent.setText("文件详情");
        }
        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        if (folderDocumentEntity.isDir()) {
            baseFragmentAdapter.bindTitle(true, Arrays.asList("修改历史", "内部共享", "下载链接", "上传链接"));
            baseFragmentAdapter.bindData(true,
                    Arrays.asList(FileChangeHistoryFragment.newInstance(fromRepoId),
                            FileInnerShareFragment.newInstance(fromRepoId, fromRepoFilePath),
                            FileLinkFragment.newInstance(fromRepoId, fromRepoFilePath, 0),
                            FileLinkFragment.newInstance(fromRepoId, fromRepoFilePath, 1)));
        } else {
            baseFragmentAdapter.bindTitle(true, Arrays.asList("历史版本", "下载链接"));
            baseFragmentAdapter.bindData(true,
                    Arrays.asList(FileVersionListFragment.newInstance(fromRepoId, fromRepoFilePath),
                            FileLinkFragment.newInstance(fromRepoId,
                                    fromRepoFilePath,
                                    0)));
        }
        getData(true);
    }

    /**
     * 获取文件对应图标
     *
     * @param fileName
     * @return
     */
    public static int getFileIcon(String fileName) {
        return FileUtils.getSFileIcon(fileName);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getSFileApi().fileVersionQuery(fromRepoId, fromRepoFilePath)
                .enqueue(new SFileCallBack<FileVersionCommits>() {
                    @Override
                    public void onSuccess(Call<FileVersionCommits> call, Response<FileVersionCommits> response) {
                        if (response.body().commits != null) {
                            int maxVersion = response.body().commits.size();
                            if (fileVersionTv == null) return;
                            fileVersionTv.setText(String.format("v%s", maxVersion));
                        }
                    }
                });
    }

    @OnClick({R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
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
