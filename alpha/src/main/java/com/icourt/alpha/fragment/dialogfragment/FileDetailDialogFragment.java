package com.icourt.alpha.fragment.dialogfragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.entity.bean.FileVersionCommits;
import com.icourt.alpha.entity.bean.FileVersionEntity;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.fragment.FileLinkFragment;
import com.icourt.alpha.fragment.FileVersionListFragment;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.interfaces.OnFragmentDataChangeListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.widget.comparators.LongFieldEntityComparator;
import com.icourt.alpha.widget.comparators.ORDER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description   文件详情
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/9
 * version 2.1.0
 */
public class FileDetailDialogFragment extends FileDetailsBaseDialogFragment implements OnFragmentDataChangeListener {
    BaseFragmentAdapter baseFragmentAdapter;
    FolderDocumentEntity folderDocumentEntity;
    String fromRepoId, fromRepoDirPath;

    public static void show(@NonNull String fromRepoId,
                            String fromRepoFilePath,
                            FolderDocumentEntity folderDocumentEntity,
                            @NonNull FragmentManager fragmentManager) {
        if (folderDocumentEntity == null) return;
        if (fragmentManager == null) return;
        String tag = FileDetailDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        show(newInstance(fromRepoId, fromRepoFilePath, folderDocumentEntity), tag, mFragTransaction);
    }


    public static FileDetailDialogFragment newInstance(
            String fromRepoId,
            String fromRepoFilePath,
            FolderDocumentEntity folderDocumentEntity) {
        FileDetailDialogFragment fragment = new FileDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putString(KEY_SEA_FILE_DIR_PATH, fromRepoFilePath);
        args.putSerializable("data", folderDocumentEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        super.initView();
        fromRepoId = getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, "");
        fromRepoDirPath = getArguments().getString(KEY_SEA_FILE_DIR_PATH, "");
        folderDocumentEntity = (FolderDocumentEntity) getArguments().getSerializable("data");
        if (folderDocumentEntity == null) return;

        fileTitleTv.setText(folderDocumentEntity.name);
        fileSizeTv.setText(FileUtils.bFormat(folderDocumentEntity.size));
        fileTypeIv.setImageResource(getFileIcon(folderDocumentEntity.name));
        titleContent.setText("文件详情");


        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        baseFragmentAdapter.bindTitle(true, Arrays.asList("历史版本", "下载链接"));
        String filePath = String.format("%s%s", fromRepoDirPath, folderDocumentEntity.name);
        baseFragmentAdapter.bindData(true,
                Arrays.asList(FileVersionListFragment.newInstance(fromRepoId, filePath),
                        FileLinkFragment.newInstance(fromRepoId,
                                filePath,
                                0)));
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
        String filePath = String.format("%s%s", fromRepoDirPath, folderDocumentEntity.name);
        getSFileApi().fileVersionQuery(fromRepoId, filePath)
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

    @Override
    public void onFragmentDataChanged(Fragment fragment, int type, Object o) {
        if (fragment instanceof FileVersionListFragment) {
            List<FileVersionEntity> fileVersionEntities = new ArrayList<>();
            try {
                fileVersionEntities.addAll((List<FileVersionEntity>) o);
                Collections.sort(fileVersionEntities, new LongFieldEntityComparator<FileVersionEntity>(ORDER.DESC));
            } catch (Exception e) {
            }
            if (fileUpdateInfoTv != null) {
                FileVersionEntity fileVersionEntityCreate = null, fileVersionEntityNewly = null;
                if (fileVersionEntities.size() > 1) {
                    fileVersionEntityCreate = fileVersionEntities.get(fileVersionEntities.size() - 1);
                    fileVersionEntityNewly = fileVersionEntities.get(0);
                } else if (fileVersionEntities.size() == 1) {
                    fileVersionEntityCreate = fileVersionEntities.get(0);
                }
                if (fileVersionEntityCreate != null) {
                    fileCreateInfoTv.setText(String.format("%s 创建于 %s",
                            fileVersionEntityCreate.user_info != null ? fileVersionEntityCreate.user_info.name : "",
                            DateUtils.getyyyyMMddHHmm(fileVersionEntityCreate.ctime * 1_000)));
                } else {
                    fileCreateInfoTv.setText("");
                }
                if (fileVersionEntityNewly != null) {
                    fileUpdateInfoTv.setText(String.format("%s 更新于 %s",
                            fileVersionEntityCreate.user_info != null ? fileVersionEntityNewly.user_info.name : "",
                            DateUtils.getyyyyMMddHHmm(fileVersionEntityNewly.ctime * 1_000)));
                } else {
                    fileUpdateInfoTv.setText("");
                }
            }
        }
    }
}
