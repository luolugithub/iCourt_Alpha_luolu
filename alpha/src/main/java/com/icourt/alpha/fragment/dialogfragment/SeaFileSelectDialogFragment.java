package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.entity.bean.RepoTypeEntity;
import com.icourt.alpha.entity.bean.SeaFileSelectParam;
import com.icourt.alpha.fragment.RepoNavigationFragment;
import com.icourt.alpha.fragment.RepoSelectListFragment;
import com.icourt.alpha.fragment.SeaFileSelectFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/11
 * version 2.1.0
 */
public class SeaFileSelectDialogFragment extends BaseDialogFragment implements OnFragmentCallBackListener {

    private static final String KEY_REPO_TYPE = "repoType";

    @BindView(R.id.titleBack)
    CheckedTextView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.foldr_go_parent_tv)
    TextView foldrGoParentTv;
    @BindView(R.id.foldr_parent_tv)
    TextView foldrParentTv;
    @BindView(R.id.dir_path_title_layout)
    RelativeLayout dirPathTitleLayout;
    @BindView(R.id.main_fl_content)
    FrameLayout mainFlContent;
    Unbinder unbinder;
    Fragment currFragment;
    HashSet<FolderDocumentEntity> selectedFolderDocumentEntities = new HashSet<>();
    SeaFileSelectParam iSeaFileSelectParams;

    public static SeaFileSelectDialogFragment newInstance() {
        SeaFileSelectDialogFragment fragment = new SeaFileSelectDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_sea_file_select, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog()
                    .getWindow()
                    .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
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

        replaceRepoTypeFragment();
    }

    /**
     * 替换资料库类型界面
     */
    private void replaceRepoTypeFragment() {
        dirPathTitleLayout.setVisibility(View.GONE);
        currFragment = addOrShowFragment(
                RepoNavigationFragment.newInstance(),
                currFragment,
                R.id.main_fl_content);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.titleBack,
            R.id.foldr_go_parent_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                dismiss();
                break;
            case R.id.foldr_go_parent_tv:
                if (canBackRepoType()) {
                    replaceRepoTypeFragment();
                } else if (canBack2ParentDir()) {
                    back2ParentDir();
                } else {
                    replaceRepoListFragmemt();
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 是否可以返回到sfile父目录
     *
     * @return
     */
    private boolean canBack2ParentDir() {
        String dirPath = iSeaFileSelectParams.getDstRepoDirPath();
        if (TextUtils.equals(dirPath, "/")) {
            return false;
        }
        return true;
    }

    /**
     * 是否返回到资料库类型导航页面
     *
     * @return
     */
    private boolean canBackRepoType() {
        return iSeaFileSelectParams == null;
    }

    /**
     * 获取sfile父目录名字
     *
     * @return
     */
    private String getDstParentDirName() {
        String dirPath = iSeaFileSelectParams.getDstRepoDirPath();
        String spilteStr = "/";
        String[] split = dirPath.split(spilteStr);
        if (split != null && split.length > 0) {
            return split[split.length - 1];
        }
        return null;
    }

    /**
     * 返回到sfile父目录
     */
    private void back2ParentDir() {
        String dirPath = iSeaFileSelectParams.getDstRepoDirPath();
        String spilteStr = "/";
        if (dirPath.endsWith(spilteStr)) {
            String[] split = dirPath.split(spilteStr);
            if (split != null && split.length > 1) {
                String lastDir = split[split.length - 1];
                String parentDir = dirPath.substring(0, dirPath.length() - (lastDir.length() + spilteStr.length()));
                SeaFileSelectParam seaFileSelectParam = new SeaFileSelectParam(iSeaFileSelectParams.getRepoType(),
                        iSeaFileSelectParams.getRepoName(),
                        iSeaFileSelectParams.getDstRepoId(),
                        parentDir,
                        selectedFolderDocumentEntities);
                replaceFolderFragment(seaFileSelectParam);
            }
        }
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof SeaFileSelectFragment && params != null) {
            SeaFileSelectParam iSeaFileSelectParams = (SeaFileSelectParam) params.getSerializable(KEY_FRAGMENT_RESULT);

            selectedFolderDocumentEntities.addAll(iSeaFileSelectParams.getSelectedFolderDocuments());
            replaceFolderFragment(iSeaFileSelectParams);
        } else if (fragment instanceof RepoNavigationFragment && params != null) {
            RepoTypeEntity repoTypeEntity = (RepoTypeEntity) params.getSerializable(KEY_FRAGMENT_RESULT);
            getArguments().putInt(KEY_REPO_TYPE, repoTypeEntity.repoType);
            foldrParentTv.setText(repoTypeEntity.title);
            replaceRepoListFragmemt();
        } else if (fragment instanceof RepoSelectListFragment && params != null) {
            RepoEntity repoEntity = (RepoEntity) params.getSerializable(KEY_FRAGMENT_RESULT);
            int repoType = params.getInt(RepoSelectListFragment.KEY_REPO_TYPE);
            SeaFileSelectParam seaFileSelectParam = new SeaFileSelectParam(SFileConfig.convert2RepoType(repoType),
                    repoEntity.repo_name,
                    repoEntity.repo_id,
                    "/",
                    selectedFolderDocumentEntities);
            replaceFolderFragment(seaFileSelectParam);
        }
    }

    /**
     * 替换资料库选择页面
     */
    private void replaceRepoListFragmemt() {
        dirPathTitleLayout.setVisibility(View.VISIBLE);
        this.iSeaFileSelectParams = null;
        currFragment = addOrShowFragment(
                RepoSelectListFragment.newInstance(SFileConfig.convert2RepoType(getArguments().getInt(KEY_REPO_TYPE))),
                currFragment,
                R.id.main_fl_content);
    }

    /**
     * 替换文档目录视图
     */
    private void replaceFolderFragment(SeaFileSelectParam iSeaFileSelectParams) {
        this.iSeaFileSelectParams = iSeaFileSelectParams;
        currFragment = addOrShowFragment(
                SeaFileSelectFragment.newInstance(iSeaFileSelectParams),
                currFragment,
                R.id.main_fl_content);
    }
}
