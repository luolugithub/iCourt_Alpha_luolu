package com.icourt.alpha.fragment.dialogfragment;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.fragment.FileInnerShareFragment;
import com.icourt.alpha.fragment.FileLinkFragment;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.widget.comparators.FileSortComparator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  文件夹详情
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/21
 * version 2.1.0
 */
public class FolderDetailDialogFragment extends FileDetailsBaseDialogFragment {
    protected static final String KEY_LOCATION_TAB_INDEX = "locationPage";//定位的tab

    public static void show(@NonNull String fromRepoId,
                            String fromRepoFileDirPath,
                            String fileName,
                            @IntRange(from = 0, to = 2) int locationTabIndex,
                            @SFileConfig.FILE_PERMISSION String repoPermission,
                            @NonNull FragmentManager fragmentManager) {
        if (fragmentManager == null) return;
        String tag = FileDetailDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        show(newInstance(fromRepoId, fromRepoFileDirPath, fileName, locationTabIndex, repoPermission), tag, mFragTransaction);
    }

    public static FolderDetailDialogFragment newInstance(
            String fromRepoId,
            String fromRepoFileDirPath,
            String fileName,
            @IntRange(from = 0, to = 2) int locationTabIndex,
            @SFileConfig.FILE_PERMISSION String repoPermission) {
        FolderDetailDialogFragment fragment = new FolderDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_DIR_PATH, fromRepoFileDirPath);
        args.putString(KEY_SEA_FILE_NAME, fileName);
        args.putInt(KEY_LOCATION_TAB_INDEX, locationTabIndex);
        args.putString(KEY_SEA_FILE_REPO_PERMISSION, repoPermission);
        fragment.setArguments(args);
        return fragment;
    }

    BaseFragmentAdapter baseFragmentAdapter;
    String fromRepoId, fromRepoDirPath;

    @Override
    protected void initView() {
        super.initView();
        fromRepoId = getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, "");
        fromRepoDirPath = getArguments().getString(KEY_SEA_FILE_DIR_PATH, "");


        fileTitleTv.setText(getArguments().getString(KEY_SEA_FILE_NAME, ""));
        fileSizeTv.setText(FileUtils.bFormat(getArguments().getLong(KEY_SEA_FILE_SIZE, 0)));
        fileTypeIv.setImageResource(R.mipmap.folder);
        titleContent.setText("文件夹详情");
        fileVersionTv.setVisibility(View.GONE);

        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        baseFragmentAdapter.bindTitle(true, Arrays.asList("内部共享", "下载链接", "上传链接"));
        String folderPath = String.format("%s%s/", fromRepoDirPath, getArguments().getString(KEY_SEA_FILE_NAME, ""));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(FileInnerShareFragment.newInstance(fromRepoId, folderPath, getRepoPermission()),
                        FileLinkFragment.newInstance(fromRepoId, folderPath, FileLinkFragment.LINK_TYPE_DOWNLOAD, getRepoPermission()),
                        FileLinkFragment.newInstance(fromRepoId, folderPath, FileLinkFragment.LINK_TYPE_UPLOAD, getRepoPermission())));
        int tabIndex = getArguments().getInt(KEY_LOCATION_TAB_INDEX);
        if (tabIndex < baseFragmentAdapter.getCount()) {
            viewPager.setCurrentItem(tabIndex);
        }
        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        String folderPath = String.format("%s%s/", fromRepoDirPath, getArguments().getString(KEY_SEA_FILE_NAME, ""));
        callEnqueue(getSFileApi().documentDirQuery(
                fromRepoId,
                folderPath),
                new SFileCallBack<List<FolderDocumentEntity>>() {
                    @Override
                    public void onSuccess(Call<List<FolderDocumentEntity>> call, Response<List<FolderDocumentEntity>> response) {
                        if (response.body() != null
                                && !response.body().isEmpty()
                                && fileSizeTv != null) {
                            try {
                                Collections.sort(response.body(), new FileSortComparator(FileSortComparator.FILE_SORT_TYPE_UPDATE));
                            } catch (Throwable e) {
                                e.printStackTrace();
                                bugSync("排序异常", e);
                            }
                            int dirNum = 0, fileNum = 0;
                            for (int i = 0; i < response.body().size(); i++) {
                                FolderDocumentEntity folderDocumentEntity = response.body().get(i);
                                if (folderDocumentEntity != null) {
                                    if (folderDocumentEntity.isDir()) {
                                        dirNum += 1;
                                    } else {
                                        fileNum += 1;
                                    }
                                }
                            }
                            FolderDocumentEntity folderDocumentEntity = response.body().get(0);
                            if (folderDocumentEntity != null&& !TextUtils.isEmpty(folderDocumentEntity.modifier_name)) {
                                fileUpdateInfoTv.setText("");
                                fileCreateInfoTv.setText(String.format("%s 更新于 %s", StringUtils.getEllipsizeText(folderDocumentEntity.modifier_name, 8), DateUtils.getyyyyMMddHHmm(folderDocumentEntity.mtime * 1_000)));
                            } else {
                                fileUpdateInfoTv.setText("");
                                fileCreateInfoTv.setText("");
                            }
                            updateFolderSize(dirNum, fileNum);
                        } else {
                            updateFolderSize(0, 0);
                            fileUpdateInfoTv.setText("");
                            fileCreateInfoTv.setText("");
                        }
                    }
                });
    }


    private void updateFolderSize(int dirNum, int fileNum) {
        if (fileSizeTv != null) {
            fileSizeTv.setText(String.format("%s个文件夹, %s个文件", dirNum, fileNum));
        }
    }
}
