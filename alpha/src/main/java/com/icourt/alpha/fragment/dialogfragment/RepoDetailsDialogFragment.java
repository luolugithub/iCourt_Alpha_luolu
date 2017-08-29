package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IntRange;
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
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FileChangedHistoryEntity;
import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.fragment.FileChangeHistoryFragment;
import com.icourt.alpha.fragment.FileInnerShareFragment;
import com.icourt.alpha.fragment.FileTrashListFragment;
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
 * date createTime：2017/8/19
 * version 2.1.0
 */
public class RepoDetailsDialogFragment extends BaseDialogFragment
        implements OnFragmentDataChangeListener {
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
    Unbinder unbinder;
    BaseFragmentAdapter baseFragmentAdapter;
    String fromRepoId;
    protected static final String KEY_SEA_FILE_FROM_REPO_ID = "seaFileFromRepoId";//原仓库id
    protected static final String KEY_LOCATION_TAB_INDEX = "locationPage";//定位的tab
    protected static final String KEY_SEA_FILE_REPO_PERMISSION = "seaFileRepoPermission";//repo的权限

    public static void show(@SFileConfig.REPO_TYPE int repoType,
                            String fromRepoId,
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
        show(newInstance(repoType, fromRepoId, locationTabIndex, repoPermission), tag, mFragTransaction);
    }


    public static RepoDetailsDialogFragment newInstance(@SFileConfig.REPO_TYPE int repoType,
                                                        String fromRepoId,
                                                        @IntRange(from = 0, to = 2) int locationTabIndex,
                                                        @SFileConfig.FILE_PERMISSION String repoPermission) {
        RepoDetailsDialogFragment fragment = new RepoDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_SEA_FILE_FROM_REPO_ID, fromRepoId);
        args.putInt(KEY_LOCATION_TAB_INDEX, locationTabIndex);
        args.putString(KEY_SEA_FILE_REPO_PERMISSION, repoPermission);
        args.putInt("repoType", repoType);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * repo 的权限
     *
     * @return
     */
    @SFileConfig.FILE_PERMISSION
    protected String getRepoPermission() {
        String stringPermission = getArguments().getString(KEY_SEA_FILE_REPO_PERMISSION, "");
        return SFileConfig.convert2filePermission(stringPermission);
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
        fromRepoId = getArguments().getString(KEY_SEA_FILE_FROM_REPO_ID, "");
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.windowAnimations = R.style.SlideAnimBottom;
                window.setAttributes(attributes);
            }
        }
        titleContent.setText("资料库详情");
        fileVersionTv.setVisibility(View.GONE);
        fileCreateInfoTv.setVisibility(View.GONE);
        fileTypeIv.setImageResource(R.mipmap.ic_document);
        fileUpdateInfoTv.setText("");

        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        baseFragmentAdapter.bindTitle(true, Arrays.asList("修改历史", "内部共享", "回收站"));
        baseFragmentAdapter.bindData(true,
                Arrays.asList(FileChangeHistoryFragment.newInstance(
                        SFileConfig.convert2RepoType(getArguments().getInt("repoType")),
                        fromRepoId,
                        getRepoPermission()),
                        FileInnerShareFragment.newInstance(fromRepoId, "/", getRepoPermission()),
                        FileTrashListFragment.newInstance(fromRepoId, "/", getRepoPermission())));

        int tabIndex = getArguments().getInt(KEY_LOCATION_TAB_INDEX);
        if (tabIndex < baseFragmentAdapter.getCount()) {
            viewPager.setCurrentItem(tabIndex);
        }
        getData(true);
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
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getSFileApi().repoDetailsQuery(fromRepoId)
                .enqueue(new SFileCallBack<RepoEntity>() {
                    @Override
                    public void onSuccess(Call<RepoEntity> call, Response<RepoEntity> response) {
                        if (fileTitleTv != null) {
                            fileTitleTv.setText(response.body().repo_name);
                            fileSizeTv.setText(FileUtils.bFormat(response.body().size));
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onFragmentDataChanged(Fragment fragment, int type, Object o) {
        if (fragment instanceof FileChangeHistoryFragment) {
            List<FileChangedHistoryEntity> fileVersionEntities = new ArrayList<>();
            try {
                fileVersionEntities.addAll((List<FileChangedHistoryEntity>) o);
                Collections.sort(fileVersionEntities, new LongFieldEntityComparator<FileChangedHistoryEntity>(ORDER.DESC));
            } catch (Exception e) {
            }
            if (fileUpdateInfoTv != null
                    && !fileVersionEntities.isEmpty()
                    && fileVersionEntities.get(0) != null) {
                FileChangedHistoryEntity fileVersionEntity = fileVersionEntities.get(0);
                fileUpdateInfoTv.setText(String.format("%s 更新于 %s", fileVersionEntity.operator_name, DateUtils.getyyyyMMddHHmm(fileVersionEntity.date)));
            } else {
                if (fileUpdateInfoTv != null) {
                    fileUpdateInfoTv.setText("");
                }
            }
        }
    }
}
