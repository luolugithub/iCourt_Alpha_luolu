package com.icourt.alpha.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseUmengActivity;
import com.icourt.alpha.constants.Const;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.fragment.dialogfragment.ContactShareDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.FolderTargetListDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSaveFileDialogFragment;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.StringUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.ArrayList;

import static com.icourt.alpha.utils.FileUtils.isFileExists;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/19
 * version 2.1.0
 */
public class ImageViewBaseActivity extends BaseUmengActivity {

    protected class LoadingDownloadListener extends FileDownloadSampleListener {
        @CallSuper
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.pending(task, soFarBytes, totalBytes);
            showLoadingDialog(null);
        }

        @CallSuper
        @Override
        protected void completed(BaseDownloadTask task) {
            super.completed(task);
            if (task != null && !TextUtils.isEmpty(task.getPath())) {
                try {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(task.getPath()))));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            dismissLoadingDialog();
        }

        @CallSuper
        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            super.paused(task, soFarBytes, totalBytes);
            dismissLoadingDialog();
        }

        @CallSuper
        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            super.error(task, e);
            dismissLoadingDialog();
            log("----------->图片下载异常:" + StringUtils.throwable2string(e));
            bugSync("下载异常", e);
            showTopSnackBar(String.format("下载异常!" + StringUtils.throwable2string(e)));
        }

        @CallSuper
        @Override
        protected void warn(BaseDownloadTask task) {
            super.warn(task);
            dismissLoadingDialog();
        }
    }

    private LoadingDownloadListener loadingDownloadListener;

    /**
     * 分享网络文件
     * 如果本地已经有了 就分享本地文件
     *
     * @param httpUrl
     * @param cacheFullPath
     */
    protected final void shareHttpFile(String httpUrl, String cacheFullPath) {
        if (checkAcessFilePermission()) {
            if (isFileExists(cacheFullPath)) {
                shareFileWithAndroid(cacheFullPath);
            } else {
                //下载完成后 再保存到资料库
                downloadFile(httpUrl,
                        cacheFullPath,
                        loadingDownloadListener = new LoadingDownloadListener() {
                            @Override
                            protected void completed(BaseDownloadTask task) {
                                super.completed(task);
                                if (task != null) {
                                    shareFileWithAndroid(task.getPath());
                                }
                            }
                        });
            }
        } else {
            requestAcessFilePermission();
        }
    }

    /**
     * 下载文件
     *
     * @param url
     * @param cacheFullPath
     */
    protected final void downloadFile(String url, String cacheFullPath) {
        downloadFile(url, cacheFullPath, new LoadingDownloadListener() {
            @Override
            protected void completed(BaseDownloadTask task) {
                super.completed(task);
                showTopSnackBar("保存成功!");
            }
        });
    }

    /**
     * 文件下载
     * 1.新请求权限
     *
     * @param url
     * @param cacheFullPath
     * @param fileDownloadListener
     */
    protected final void downloadFile(String url, String cacheFullPath, FileDownloadListener fileDownloadListener) {
        if (checkAcessFilePermission()) {
            if (TextUtils.isEmpty(url)) {
                showTopSnackBar("下载地址为null");
                return;
            }
            if (!FileUtils.sdAvailable()) {
                showTopSnackBar(R.string.str_sd_unavailable);
                return;
            }
            if (isFileExists(cacheFullPath)) {
                showTopSnackBar("文件已保存");
                return;
            }
            FileDownloader
                    .getImpl()
                    .create(url)
                    .setPath(cacheFullPath)
                    .setListener(fileDownloadListener).start();
        } else {
            requestAcessFilePermission();
        }
    }

    /**
     * 将网络文件分享到资料库中去
     *
     * @param url
     * @param cacheFullPath
     */
    protected final void shareHttpFile2repo(String url, String cacheFullPath) {
        if (checkAcessFilePermission()) {
            if (isFileExists(cacheFullPath)) {
                shareImport2repo(cacheFullPath);
            } else {
                //下载完成后 再保存到资料库
                downloadFile(url, cacheFullPath, loadingDownloadListener = new LoadingDownloadListener() {
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        if (task != null) {
                            shareImport2repo(task.getPath());
                        }
                    }
                });
            }
        } else {
            requestAcessFilePermission();
        }
    }

    /**
     * 转发给同事
     * 1.本地没有 就下载
     * 2.本地有 就立即分享
     */
    protected final void shareHttpFile2Friends(String url, final String cacheFullPath) {
        if (checkAcessFilePermission()) {
            if (isFileExists(cacheFullPath)) {
                showContactShareDialogFragment(cacheFullPath);
            } else {
                //下载完成后 再保存到资料库
                downloadFile(url,
                        cacheFullPath,
                        loadingDownloadListener = new LoadingDownloadListener() {
                            @Override
                            protected void completed(BaseDownloadTask task) {
                                super.completed(task);
                                showContactShareDialogFragment(cacheFullPath);
                            }
                        });
            }
        } else {
            requestAcessFilePermission();
        }
    }

    /**
     * 展示联系人转发对话框
     *
     * @param filePath
     */
    private void showContactShareDialogFragment(String filePath) {
        String tag = ContactShareDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ContactShareDialogFragment.newInstanceFile(filePath, true)
                .show(mFragTransaction, tag);
    }

    /**
     * 保存到我的文档
     */
    private void shareImport2repo(String localFilePath) {
        String tag = FolderTargetListDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ArrayList<String> selectedFileNames = new ArrayList<>();
        FolderTargetListDialogFragment.newInstance(
                Const.FILE_ACTION_ADD,
                SFileConfig.REPO_MINE,
                null,
                null,
                null,
                null,
                selectedFileNames,
                localFilePath)
                .show(mFragTransaction, tag);
    }

    /**
     * 分享到项目
     *
     * @param url
     * @param cacheFullPath
     */
    protected final void shareHttpFile2Project(String url, final String cacheFullPath) {
        if (checkAcessFilePermission()) {
            if (isFileExists(cacheFullPath)) {
                shareImport2Project(cacheFullPath);
            } else {
                //下载完成后 再保存到资料库
                downloadFile(url,
                        cacheFullPath,
                        loadingDownloadListener = new LoadingDownloadListener() {
                            @Override
                            protected void completed(BaseDownloadTask task) {
                                super.completed(task);
                                shareImport2Project(cacheFullPath);
                            }
                        });
            }
        } else {
            requestAcessFilePermission();
        }
    }

    /**
     * 保存到项目
     */
    private void shareImport2Project(String localFilePath) {
        String tag = ProjectSaveFileDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        mFragTransaction.add(ProjectSaveFileDialogFragment.newInstance(localFilePath, ProjectSaveFileDialogFragment.OTHER_TYPE), tag);
        mFragTransaction.commitAllowingStateLoss();
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        if (loadingDownloadListener != null) {
            try {
                FileDownloader
                        .getImpl()
                        .pause(loadingDownloadListener);
            } catch (Exception e) {
            }
        }
        super.onDestroy();
    }
}
