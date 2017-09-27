package com.icourt.alpha.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.constants.DownloadConfig;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.ISeaFile;
import com.icourt.alpha.fragment.dialogfragment.ContactShareDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.FileDetailDialogFragment;
import com.icourt.alpha.http.HttpThrowableUtils;
import com.icourt.alpha.http.IDefNotify;
import com.icourt.alpha.http.callback.SFileCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.view.ProgressLayout;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.SFileConfig.FILE_FROM_IM;
import static com.icourt.alpha.constants.SFileConfig.FILE_FROM_TASK;
import static com.icourt.alpha.constants.SFileConfig.PERMISSION_RW;

/**
 * Description 文件下载界面
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/24
 * version 2.1.0
 */
public class FileDownloadActivity extends ImageViewBaseActivity {

    private static final String KEY_SEA_FILE_FROM = "key_sea_file_from";
    private static final String KEY_SEA_FILE = "key_sea_file";
    private static final String KEY_INTERCEPT_LOOK_FILE_DETAILS = "key_intercept_look_file_details";//是否拦截查看文件详情

    @BindView(R.id.download_notice_tv)
    TextView downloadNoticeTv;
    @BindView(R.id.file_open_tv)
    TextView fileOpenTv;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    ImageView titleAction;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.file_type_icon)
    ImageView fileTypeIcon;
    @BindView(R.id.file_name_tv)
    TextView fileNameTv;
    @BindView(R.id.file_size_tv)
    TextView fileSizeTv;
    @BindView(R.id.download_progressbar)
    ProgressLayout downloadProgressbar;
    @BindView(R.id.download_layout)
    LinearLayout downloadLayout;
    @BindView(R.id.download_continue_tv)
    TextView downloadContinueTv;
    @BindView(R.id.download_cancel_tv)
    ImageView downloadCancelTv;

    private String fileCachePath = "";//保存路径
    private String fileDownloadUrl = "";//下载的地址


    public static <T extends ISeaFile> void launch(@NonNull Context context, T seaFile, @SFileConfig.FILE_FROM int fileFrom) {
        launch(context, seaFile, fileFrom, false);
    }

    public static <T extends ISeaFile> void launch(@NonNull Context context,
                                                   T seaFile,
                                                   @SFileConfig.FILE_FROM int fileFrom,
                                                   boolean interceptLookFileDetails) {
        if (context == null) return;
        if (seaFile == null) return;
        Intent intent = new Intent(context, FileDownloadActivity.class);
        intent.putExtra(KEY_SEA_FILE, seaFile);
        intent.putExtra(KEY_SEA_FILE_FROM, fileFrom);
        intent.putExtra(KEY_INTERCEPT_LOOK_FILE_DETAILS, interceptLookFileDetails);
        context.startActivity(intent);
    }

    int fileFrom;
    ISeaFile iSeaFile;
    String fileName;

    private FileDownloadListener fileDownloadListener = new FileDownloadListener() {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            updateViewState(0);
            if (downloadProgressbar != null) {
                downloadProgressbar.setMaxProgress(100);
            }
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            if (downloadProgressbar != null) {
                int total = totalBytes;
                if (total <= 0) {
                    if (soFarBytes > 0) {
                        total = soFarBytes * 3;
                    } else {
                        total = 100;
                    }
                }
                int progress = (int) ((soFarBytes * 1.0f / total * 1.0f) * 100);
                downloadProgressbar.setMaxProgress(100);
                downloadProgressbar.setCurrentProgress(progress);
            }
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            if (downloadProgressbar != null) {
                downloadProgressbar.setCurrentProgress(100);
                updateViewState(2);
                openImageView();
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            log("------------->下载异常:" + StringUtils.throwable2string(e));
            HttpThrowableUtils.handleHttpThrowable(new IDefNotify() {
                @Override
                public void defNotify(String noticeStr) {
                    showTopSnackBar(noticeStr);
                }
            }, e);
            bugSync("下载异常", e);
        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        iSeaFile = (ISeaFile) getIntent().getSerializableExtra(KEY_SEA_FILE);
        fileFrom = getIntent().getIntExtra(KEY_SEA_FILE_FROM, SFileConfig.FILE_FROM_REPO);
        fileCachePath = buildSavePath();

        fileName = FileUtils.getFileName(iSeaFile.getSeaFileFullPath());
        setTitle(fileName);
        fileSizeTv.setText(String.format("(%s)", FileUtils.bFormat(iSeaFile.getSeaFileSize())));
        fileNameTv.setText(fileName);
        fileTypeIcon.setImageResource(FileUtils.getSFileIcon(fileName));
        ImageView titleActionImage = getTitleActionImage();
        if (titleActionImage != null) {
            titleActionImage.setImageResource(R.mipmap.header_icon_more);
        }

        //可能有bug
        if (FileUtils.isFileExists(fileCachePath)) {
            openImageView();
            updateViewState(2);
        } else {
            updateViewState(-1);
            //非图片 不主动下载
            if (IMUtils.isPIC(fileName)) {
                startDownload();
            }
        }
    }

    private void startDownload() {
        //检查文件写入权限
        if (checkAcessFilePermission()) {
            getData(true);
        } else {
            requestAcessFilePermission();
        }
    }

    private void openImageView() {
        if (IMUtils.isPIC(fileCachePath)) {
            ArrayList<ISeaFile> seaFileImages = new ArrayList<>();
            seaFileImages.add(iSeaFile);
            ImageViewerActivity.launch(
                    getContext(),
                    SFileConfig.convert2FileFrom(fileFrom),
                    seaFileImages,
                    0,
                    getIntent().getBooleanExtra(KEY_INTERCEPT_LOOK_FILE_DETAILS,false)
            );
            finish();
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        showLoadingDialog(null);
        switch (fileFrom) {
            case SFileConfig.FILE_FROM_IM: {//享聊的下载地址要单独获取 用资料库下载地址 提示没权限
                callEnqueue(
                        getChatApi().fileUrlQuery(
                                iSeaFile.getSeaFileRepoId(),
                                FileUtils.getFileParentDir(iSeaFile.getSeaFileFullPath()),
                                fileName),
                        new SimpleCallBack<JsonElement>() {
                            @Override
                            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                                dismissLoadingDialog();
                                if (response.body() != null) {
                                    String downloadUrl = response.body().message;
                                    downloadFile(downloadUrl);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                                super.onFailure(call, t);
                                dismissLoadingDialog();
                            }
                        });
            }
            break;
            default: {
                callEnqueue(
                        getSFileApi().sFileDownloadUrlQuery(
                                iSeaFile.getSeaFileRepoId(),
                                iSeaFile.getSeaFileFullPath(),
                                iSeaFile.getSeaFileVersionId()),
                        new SFileCallBack<String>() {
                            @Override
                            public void onSuccess(Call<String> call, Response<String> response) {
                                dismissLoadingDialog();
                                fileDownloadUrl = response.body();
                                if (isDestroyOrFinishing()) return;
                                downloadFile(fileDownloadUrl);
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                super.onFailure(call, t);
                                dismissLoadingDialog();
                            }
                        });
            }
            break;
        }
    }

    /**
     * @param state -1未下载 0下载中 1暂停中 2:下载完成
     */
    private void updateViewState(@IntRange(from = -1, to = 2) int state) {
        switch (state) {
            case -1:
                titleAction.setEnabled(false);
                titleAction.setImageResource(R.mipmap.more_disabled);
                fileOpenTv.setVisibility(View.GONE);
                downloadLayout.setVisibility(View.GONE);
                downloadContinueTv.setVisibility(View.GONE);
                downloadNoticeTv.setVisibility(View.VISIBLE);
                break;
            case 0:
                titleAction.setEnabled(false);
                titleAction.setImageResource(R.mipmap.more_disabled);
                fileOpenTv.setVisibility(View.GONE);
                downloadLayout.setVisibility(View.VISIBLE);
                downloadContinueTv.setVisibility(View.GONE);
                downloadNoticeTv.setVisibility(View.GONE);
                break;
            case 1:
                titleAction.setEnabled(false);
                titleAction.setImageResource(R.mipmap.more_disabled);
                fileOpenTv.setVisibility(View.GONE);
                downloadLayout.setVisibility(View.GONE);
                downloadContinueTv.setVisibility(View.VISIBLE);
                downloadNoticeTv.setVisibility(View.GONE);
                break;
            case 2:
                titleAction.setEnabled(true);
                titleAction.setImageResource(R.mipmap.header_icon_more);
                fileOpenTv.setVisibility(View.VISIBLE);
                downloadLayout.setVisibility(View.GONE);
                downloadContinueTv.setVisibility(View.GONE);
                downloadNoticeTv.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 下载文件
     *
     * @param url
     */
    private void downloadFile(String url) {
        if (!FileUtils.sdAvailable()) {
            showTopSnackBar(R.string.str_sd_unavailable);
            return;
        }
        if (TextUtils.isEmpty(url)) {
            showTopSnackBar("下载地址为null");
            return;
        }

        FileDownloader
                .getImpl()
                .create(url)
                .setPath(fileCachePath)
                .setListener(fileDownloadListener)
                .start();
    }

    /**
     * 文件有历史版本
     * 默认  文件名字_versionId(hashcode) 位数比较少
     *
     * @return
     */
    private String buildSavePath() {
        return DownloadConfig.getSeaFileDownloadPath(getLoginUserId(), iSeaFile);
    }


    @OnClick({R.id.download_continue_tv,
            R.id.file_open_tv,
            R.id.download_cancel_tv,
            R.id.download_notice_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleAction:
                showBottomMenuDialog();
                break;
            case R.id.download_notice_tv:
                startDownload();
                break;
            case R.id.download_continue_tv:
                updateViewState(0);
                //地址失效 断点下载有问题
                getData(true);
                break;
            case R.id.download_cancel_tv:
                updateViewState(1);
                pauseDownload();
                break;
            case R.id.file_open_tv:
                showOpenableThirdPartyAppDialog();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 展示更多菜单
     */
    private void showBottomMenuDialog() {
        ArrayList<String> menus = new ArrayList<>(Arrays.asList(getString(R.string.sfile_file_details), "转发给同事", "保存到项目", "用其他应用打开"));
        if (TextUtils.equals(iSeaFile.getSeaFilePermission(), PERMISSION_RW)) {
            menus.add(getString(R.string.str_delete));
        }
        //聊天文件 任务附件 暂时不要文件详情
        if (fileFrom == FILE_FROM_TASK
                || fileFrom == FILE_FROM_IM) {
            menus.remove(getString(R.string.sfile_file_details));
        }
        new BottomActionDialog(getContext(),
                null,
                menus,
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        String action = adapter.getItem(position);
                        if (TextUtils.equals(action, getString(R.string.sfile_file_details))) {
                            FileDetailDialogFragment.show(
                                    SFileConfig.REPO_UNKNOW,
                                    iSeaFile,
                                    0,
                                    getSupportFragmentManager());
                        } else if (TextUtils.equals(action, "转发给同事")) {
                            showContactShareDialogFragment(fileCachePath);
                        } else if (TextUtils.equals(action, "保存到项目")) {
                            shareHttpFile2Project(null, fileCachePath);
                        } else if (TextUtils.equals(action, "用其他应用打开")) {
                            showOpenableThirdPartyAppDialog();
                        } else if (TextUtils.equals(action, getString(R.string.str_delete))) {
                            showDeleteConfirmDialog();
                        }
                    }
                }).show();
    }

    /**
     * 展示删除确认对话框
     */
    private void showDeleteConfirmDialog() {
        new BottomActionDialog(
                getContext(),
                getString(R.string.sfile_delete_confirm),
                Arrays.asList(getString(R.string.str_ok)),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        deleteFile();
                    }
                }).show();
    }

    /**
     * 删除文件
     */
    private void deleteFile() {
        final ISeaFile item = iSeaFile;
        showLoadingDialog(R.string.str_executing);
        callEnqueue(getSFileApi().fileDelete(
                item.getSeaFileRepoId(),
                item.getSeaFileFullPath()),
                new SFileCallBack<JsonObject>() {
                    @Override
                    public void onSuccess(Call<JsonObject> call, Response<JsonObject> response) {
                        dismissLoadingDialog();
                        deletCachedSeaFile(item);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        super.onFailure(call, t);
                        dismissLoadingDialog();
                    }
                });
    }

    /**
     * 删除缓存的seafile
     *
     * @param item
     */
    private void deletCachedSeaFile(ISeaFile item) {
        FileUtils.deleteFile(DownloadConfig.getSeaFileDownloadPath(getLoginUserId(), item));
    }

    /**
     * 展示联系人转发对话框
     *
     * @param filePath
     */
    public void showContactShareDialogFragment(String filePath) {
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
     * 用其他程序打开
     */
    private void showOpenableThirdPartyAppDialog() {
        if (TextUtils.isEmpty(fileCachePath)) {
            showTopSnackBar("文件不存在");
            return;
        }
        File file = new File(fileCachePath);
        if (!file.exists()) {
            showTopSnackBar("文件不存在");
            return;
        }
        if (TextUtils.equals(FileUtils.getFileType(file.getName()), ".key")) {
            showTopSnackBar("此文件类型不支持打开");
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //获取文件file的MIME类型
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            String type = FileUtils.getMIMEType(file);
            //设置intent的data和Type属性。
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            intent.setDataAndType(uri, type);
            startActivity(intent);     //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
        } catch (Exception e) {
            e.printStackTrace();
            bugSync("打开文件失败", e);
        }
    }


    @Override
    protected void onDestroy() {
        pauseDownload();
        super.onDestroy();
    }

    private void pauseDownload() {
        if (fileDownloadListener != null) {
            try {
                FileDownloader
                        .getImpl()
                        .pause(fileDownloadListener);
            } catch (Exception e) {
            }
        }
    }
}
