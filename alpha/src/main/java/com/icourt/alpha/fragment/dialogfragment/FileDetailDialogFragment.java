package com.icourt.alpha.fragment.dialogfragment;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.icourt.alpha.BuildConfig;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.FileDownloadActivity;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FileVersionEntity;
import com.icourt.alpha.entity.bean.ISeaFile;
import com.icourt.alpha.fragment.FileLinkFragment;
import com.icourt.alpha.fragment.FileVersionListFragment;
import com.icourt.alpha.interfaces.OnFragmentDataChangeListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;
import com.icourt.alpha.utils.SFileTokenUtils;
import com.icourt.alpha.utils.StringUtils;
import com.icourt.alpha.utils.UrlUtils;
import com.icourt.alpha.view.tab.AlphaTitleNavigatorAdapter;
import com.icourt.alpha.widget.comparators.LongFieldEntityComparator;
import com.icourt.alpha.widget.comparators.ORDER;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.OnClick;

/**
 * Description   文件详情
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/9
 * version 2.1.0
 */
public class FileDetailDialogFragment extends FileDetailsBaseDialogFragment
        implements OnFragmentDataChangeListener {
    BaseFragmentAdapter baseFragmentAdapter;
    protected static final String KEY_LOCATION_TAB_INDEX = "locationPage";//定位的tab
    protected static final String KEY_DATA = "data";
    final List<FileVersionEntity> fileVersionEntities = new ArrayList<>();
    ISeaFile iSeaFile;


    public static void show(
            @SFileConfig.REPO_TYPE int repoType,
            ISeaFile iSeaFile,
            @IntRange(from = 0, to = 1) int locationTabIndex,
            @NonNull FragmentManager fragmentManager) {
        if (iSeaFile == null) return;
        if (fragmentManager == null) return;
        String tag = FileDetailDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        show(newInstance(repoType, iSeaFile, locationTabIndex), tag, mFragTransaction);
    }


    public static FileDetailDialogFragment newInstance(
            @SFileConfig.REPO_TYPE int repoType,
            ISeaFile iSeaFile,
            @IntRange(from = 0, to = 1) int locationTabIndex) {
        FileDetailDialogFragment fragment = new FileDetailDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_DATA, iSeaFile);
        args.putInt(KEY_SEA_FILE_FROM_REPO_TYPE, repoType);
        args.putInt(KEY_LOCATION_TAB_INDEX, locationTabIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        super.initView();
        iSeaFile = (ISeaFile) getArguments().getSerializable(KEY_DATA);
        if (iSeaFile == null) {
            dismiss();
        }

        fileTitleTv.setText(FileUtils.getFileName(iSeaFile.getSeaFileFullPath()));
        fileSizeTv.setText(FileUtils.bFormat(iSeaFile.getSeaFileSize()));
        //图片格式 加载缩略图
        if (IMUtils.isPIC(iSeaFile.getSeaFileFullPath())) {
            GlideUtils.loadSFilePic(getContext(),
                    getSfileThumbnailImage(),
                    fileTypeIv);
        } else {
            fileTypeIv.setImageResource(getFileIcon(iSeaFile.getSeaFileFullPath()));
        }
        titleContent.setText("文件详情");


        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));
        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdapter(new AlphaTitleNavigatorAdapter(1.0f) {
            @Nullable
            @Override
            public CharSequence getTitle(int index) {
                return baseFragmentAdapter.getPageTitle(index);
            }

            @Override
            public int getCount() {
                return baseFragmentAdapter.getCount();
            }

            @Override
            public void onTabClick(View v, int pos) {
                viewPager.setCurrentItem(pos, true);
            }
        });
        tabLayout.setNavigator2(commonNavigator)
                .setupWithViewPager(viewPager);
        baseFragmentAdapter.bindTitle(true, Arrays.asList("历史版本", "下载链接"));
        String filePath = iSeaFile.getSeaFileFullPath();
        //根据类型判断 除了我的资料库里面的文件有分享链接 其他都屏蔽
        if (getRepoType() == SFileConfig.REPO_MINE) {
            baseFragmentAdapter.bindData(true,
                    Arrays.asList(
                            FileVersionListFragment.newInstance(
                                    iSeaFile.getSeaFileRepoId(),
                                    filePath,
                                    iSeaFile.getSeaFilePermission()),
                            FileLinkFragment.newInstance(
                                    iSeaFile.getSeaFileRepoId(),
                                    filePath,
                                    FileLinkFragment.LINK_TYPE_DOWNLOAD,
                                    iSeaFile.getSeaFilePermission())));
        } else {
            baseFragmentAdapter.bindData(true,
                    Arrays.asList(FileVersionListFragment.newInstance(
                            iSeaFile.getSeaFileRepoId(),
                            filePath,
                            iSeaFile.getSeaFilePermission())));
        }

        int tabIndex = getArguments().getInt(KEY_LOCATION_TAB_INDEX);
        if (tabIndex < baseFragmentAdapter.getCount()) {
            viewPager.setCurrentItem(tabIndex);
        }
    }

    /**
     * 获取缩略图地址
     *
     * @return
     */
    private String getSfileThumbnailImage() {
        //https://test.alphalawyer.cn/ilaw/api/v2/documents/thumbnailImage?repoId=d4f82446-a37f-478c-b6b5-ed0e779e1768&seafileToken=%20d6c69d6f4fc208483c243246c6973d8eb141501c&p=//1502507774237.png&size=250
        return String.format("%silaw/api/v2/documents/thumbnailImage?repoId=%s&seafileToken=%s&p=%s&size=%s",
                BuildConfig.API_URL,
                iSeaFile.getSeaFileRepoId(),
                SFileTokenUtils.getSFileToken(),
                UrlUtils.encodeUrl(iSeaFile.getSeaFileFullPath()),
                150);
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
    public void onStop() {
        super.onStop();
        if (getDialog() != null) {
            getDialog().show();
        }
    }

    @OnClick({R.id.file_title_tv,
            R.id.file_version_tv,
            R.id.file_type_iv,
            R.id.file_size_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_title_tv:
            case R.id.file_version_tv:
            case R.id.file_type_iv:
            case R.id.file_size_tv:
                if (!fileVersionEntities.isEmpty()) {
                    FileVersionEntity item = fileVersionEntities.get(0);
                    if (item == null) return;
                    item.seaFileFullPath = iSeaFile.getSeaFileFullPath();
                    item.seaFilePermission = iSeaFile.getSeaFilePermission();
                    FileDownloadActivity.launch(
                            getContext(),
                            item,
                            SFileConfig.FILE_FROM_REPO,
                            true);
                } else {
                    FileDownloadActivity.launch(
                            getContext(),
                            iSeaFile,
                            SFileConfig.FILE_FROM_REPO,
                            true);
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onFragmentDataChanged(Fragment fragment, int type, Object o) {
        if (isDetached()) return;
        if (fragment instanceof FileVersionListFragment) {
            fileVersionEntities.clear();
            try {
                fileVersionEntities.addAll((List<FileVersionEntity>) o);
                Collections.sort(fileVersionEntities, new LongFieldEntityComparator<FileVersionEntity>(ORDER.ASC));
            } catch (Exception e) {
            }
            if (fileCreateInfoTv == null) return;
            fileCreateInfoTv.setText("");
            fileUpdateInfoTv.setText("");
            if (fileVersionEntities.isEmpty()) return;
            FileVersionEntity fileVersionEntityCreate = null;
            FileVersionEntity fileVersionEntityUpdate = null;
            switch (fileVersionEntities.size()) {
                case 1:
                    fileVersionEntityCreate = fileVersionEntities.get(0);
                    if (fileVersionEntityCreate != null) {
                        fileCreateInfoTv.setText(String.format("%s 创建于 %s",
                                StringUtils.getEllipsizeText(fileVersionEntityCreate.user_info != null ? fileVersionEntityCreate.user_info.name : "", 8),
                                DateUtils.getyyyyMMddHHmm(fileVersionEntityCreate.ctime * 1_000)));
                    }
                    break;
                case 2:
                    fileVersionEntityCreate = fileVersionEntities.get(0);
                    if (fileVersionEntityCreate != null) {
                        fileCreateInfoTv.setText(String.format("%s 创建于 %s",
                                StringUtils.getEllipsizeText(fileVersionEntityCreate.user_info != null ? fileVersionEntityCreate.user_info.name : "", 8),
                                DateUtils.getyyyyMMddHHmm(fileVersionEntityCreate.ctime * 1_000)));
                    }
                    fileVersionEntityUpdate = fileVersionEntities.get(1);
                    if (fileVersionEntityUpdate != null) {
                        fileUpdateInfoTv.setText(String.format("%s 更新于 %s",
                                StringUtils.getEllipsizeText(fileVersionEntityUpdate.user_info != null ? fileVersionEntityUpdate.user_info.name : "", 8),
                                DateUtils.getyyyyMMddHHmm(fileVersionEntityUpdate.ctime * 1_000)));
                    }
                    break;
                default:
                    fileVersionEntityCreate = fileVersionEntities.get(fileVersionEntities.size() - 2);
                    if (fileVersionEntityCreate != null) {
                        fileCreateInfoTv.setText(String.format("%s 更新于 %s",
                                StringUtils.getEllipsizeText(fileVersionEntityCreate.user_info != null ? fileVersionEntityCreate.user_info.name : "", 8),
                                DateUtils.getyyyyMMddHHmm(fileVersionEntityCreate.ctime * 1_000)));
                    }
                    fileVersionEntityUpdate = fileVersionEntities.get(fileVersionEntities.size() - 1);
                    if (fileVersionEntityUpdate != null) {
                        fileUpdateInfoTv.setText(String.format("%s 更新于 %s",
                                StringUtils.getEllipsizeText(fileVersionEntityUpdate.user_info != null ? fileVersionEntityUpdate.user_info.name : "", 8),
                                DateUtils.getyyyyMMddHHmm(fileVersionEntityUpdate.ctime * 1_000)));
                    }
                    break;
            }
            fileVersionTv.setText(String.format("v%s", fileVersionEntities.size()));
        }
    }
}
