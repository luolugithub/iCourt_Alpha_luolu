package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.FolderRemoveAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.adapter.baseadapter.adapterObserver.DataChangeAdapterObserver;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.constants.SFileConfig;
import com.icourt.alpha.entity.bean.DefaultRepoEntity;
import com.icourt.alpha.entity.bean.FolderDocumentEntity;
import com.icourt.alpha.entity.bean.RepoEntity;
import com.icourt.alpha.entity.bean.RepoIdResEntity;
import com.icourt.alpha.entity.bean.RepoTypeEntity;
import com.icourt.alpha.entity.bean.SeaFileSelectParam;
import com.icourt.alpha.fragment.RepoNavigationFragment;
import com.icourt.alpha.fragment.RepoSelectListFragment;
import com.icourt.alpha.fragment.SeaFileSelectFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnSeaFileSelectListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.api.RequestUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Response;

import static com.icourt.alpha.constants.SFileConfig.REPO_MINE;
import static com.icourt.alpha.constants.SFileConfig.REPO_PROJECT;
import static com.icourt.alpha.fragment.RepoSelectListFragment.KEY_REPO_TYPE;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/11
 * version 2.1.0
 */
public class SeaFileSelectDialogFragment extends BaseDialogFragment
        implements OnFragmentCallBackListener, OnSeaFileSelectListener {
    private static final String KEY_TASK_ID = "key_task_id";
    private static final String KEY_PROJECT_ID = "key_project_id";
    private static final String KEY_PROJECT_NAME = "key_project_name";

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
    final ArrayList<FolderDocumentEntity> selectedFolderDocumentEntities = new ArrayList<>();
    SeaFileSelectParam iSeaFileSelectParams;
    RepoTypeEntity selectRepoTypeEntity;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.selected_num_tv)
    TextView selectedNumTv;
    @BindView(R.id.ok_tv)
    TextView okTv;
    @BindView(R.id.dragView)
    LinearLayout dragView;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;
    FolderRemoveAdapter selectedFolderDocumentAdapter;
    String taskId, projectId, projectName;
    @BindView(R.id.contentEmptyText)
    TextView contentEmptyText;
    @BindView(R.id.panel_indicator)
    ImageView panelIndicator;
    @BindView(R.id.bottom_action_view)
    LinearLayout bottomActionView;
    private SlidingUpPanelLayout.SimplePanelSlideListener simplePanelSlideListener;

    public static SeaFileSelectDialogFragment newInstance(@NonNull String taskId,
                                                          @Nullable String projectId,
                                                          @Nullable String projectName) {
        SeaFileSelectDialogFragment fragment = new SeaFileSelectDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TASK_ID, taskId);
        args.putString(KEY_PROJECT_ID, projectId);
        args.putString(KEY_PROJECT_NAME, projectName);
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
        taskId = getArguments().getString(KEY_TASK_ID, "");
        projectId = getArguments().getString(KEY_PROJECT_ID, "");
        projectName = getArguments().getString(KEY_PROJECT_NAME, "");
        contentEmptyText.setText(R.string.sfile_un_select);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.windowAnimations = R.style.SlideAnimBottom;
                window.setAttributes(attributes);
            }
        }
        selectRepoTypeEntity = new RepoTypeEntity(SFileConfig.REPO_MINE, "我的");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(selectedFolderDocumentAdapter = new FolderRemoveAdapter());
        selectedFolderDocumentAdapter.registerAdapterDataObserver(new DataChangeAdapterObserver() {
            @Override
            protected void updateUI() {
                contentEmptyText.setVisibility(selectedFolderDocumentAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                updateSelectNum();
            }
        });
        selectedFolderDocumentAdapter.setOnItemChildClickListener(new BaseRecyclerAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                switch (view.getId()) {
                    case R.id.document_delete_iv:
                        FolderDocumentEntity item = selectedFolderDocumentAdapter.getItem(position);
                        if (item == null) return;
                        //更新子fragment
                        if (currFragment instanceof SeaFileSelectFragment) {
                            SeaFileSelectFragment seaFileSelectFragment = (SeaFileSelectFragment) currFragment;
                            Bundle args = new Bundle();
                            args.putSerializable(KEY_FRAGMENT_UPDATE_KEY, item);
                            seaFileSelectFragment.notifyFragmentUpdate(seaFileSelectFragment, -1, args);
                        }

                        selectedFolderDocumentAdapter.removeItem(position);
                        break;
                }
            }
        });
        selectedFolderDocumentAdapter.bindData(true, new ArrayList<FolderDocumentEntity>(selectedFolderDocumentEntities));
        slidingLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePanel();
            }
        });
        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.SimplePanelSlideListener() {
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                //panelIndicator.setImageResource(newState == SlidingUpPanelLayout.PanelState.EXPANDED ? R.mipmap.panel_close : R.mipmap.panel_open);
            }
        });

        getData(true);
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        showLoadingDialog(null);
        //关联项目  直接展示项目下的文件 否则展示我的默认资料库文件
        if (!TextUtils.isEmpty(projectId)) {
            callEnqueue(getApi().projectQueryDocumentId(projectId), new SimpleCallBack2<RepoIdResEntity>() {
                @Override
                public void onSuccess(Call<RepoIdResEntity> call, Response<RepoIdResEntity> response) {
                    dismissLoadingDialog();
                    if (TextUtils.isEmpty(response.body().seaFileRepoId)) {
                        showToast("服务器返回的资料库id为null");
                        bugSync("项目repo 获取null", "projectid:" + projectId);
                    }
                    //对应项目下的第一级文件
                    SeaFileSelectParam seaFileSelectParam = new SeaFileSelectParam(
                            REPO_PROJECT,
                            projectName,
                            response.body().seaFileRepoId,
                            "/",
                            selectedFolderDocumentEntities);
                    replaceFolderFragment(seaFileSelectParam);
                }

                @Override
                public void onFailure(Call<RepoIdResEntity> call, Throwable t) {
                    super.onFailure(call, t);
                    dismissLoadingDialog();
                }
            });
        } else {
            callEnqueue(getApi().repoDefaultQuery(),
                    new SimpleCallBack<DefaultRepoEntity>() {
                        @Override
                        public void onSuccess(Call<ResEntity<DefaultRepoEntity>> call, Response<ResEntity<DefaultRepoEntity>> response) {
                            dismissLoadingDialog();
                            if (response.body().result != null) {
                                //对应项目下的第一级文件
                                SeaFileSelectParam seaFileSelectParam = new SeaFileSelectParam(
                                        REPO_MINE,
                                        "我的默认资料库",
                                        response.body().result.repoId,
                                        "/",
                                        selectedFolderDocumentEntities);
                                replaceFolderFragment(seaFileSelectParam);
                            } else {
                                bugSync("获取默认资料库失败", response.body().toString());
                                showTopSnackBar("获取默认资料库失败");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResEntity<DefaultRepoEntity>> call, Throwable t) {
                            super.onFailure(call, t);
                            dismissLoadingDialog();
                        }
                    });
        }
    }


    private void showPanel(final boolean isShow) {
        slidingLayout.removePanelSlideListener(simplePanelSlideListener);
        slidingLayout.addPanelSlideListener(simplePanelSlideListener = new SlidingUpPanelLayout.SimplePanelSlideListener() {
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    bottomActionView.setVisibility(View.VISIBLE);
                    dragView.setVisibility(View.VISIBLE);
                    mainFlContent.setPadding(0, 0, 0, DensityUtil.dip2px(getContext(), 25));
                } else if (newState == SlidingUpPanelLayout.PanelState.HIDDEN) {
                    dragView.setVisibility(View.GONE);
                    bottomActionView.setVisibility(View.GONE);
                    mainFlContent.setPadding(0, 0, 0, 0);
                }
                //panelIndicator.setImageResource(newState == SlidingUpPanelLayout.PanelState.EXPANDED ? R.mipmap.panel_close : R.mipmap.panel_open);
            }
        });
        slidingLayout.setPanelState(isShow ? SlidingUpPanelLayout.PanelState.COLLAPSED : SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    /**
     * 替换资料库类型界面
     */
    private void replaceRepoTypeFragment() {
        showPanel(false);
        this.iSeaFileSelectParams = null;
        dirPathTitleLayout.setVisibility(View.GONE);
        currFragment = addOrShowFragment(
                RepoNavigationFragment.newInstance(""),
                currFragment,
                R.id.main_fl_content);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.titleBack,
            R.id.foldr_go_parent_tv,
            R.id.selected_num_tv,
            R.id.ok_tv,
            R.id.contentEmptyText})
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
            case R.id.selected_num_tv:
                togglePanel();
                break;
            case R.id.ok_tv:
                addAttachmentFromRepo();
                break;
            case R.id.contentEmptyText:
                closePanel();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 打开面板
     */
    private void openPanel() {
        if (slidingLayout.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
    }

    /**
     * 反选打开状态
     */
    private void togglePanel() {
        if (slidingLayout.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED) {
            openPanel();
        } else {
            closePanel();
        }
    }

    /**
     * 关闭面板
     */
    private void closePanel() {
        if (slidingLayout.getPanelState()
                != SlidingUpPanelLayout.PanelState.COLLAPSED) {
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    /**
     * 传递参数
     * {
     * "pathInfoList": [
     * {
     * "filePath": "/1504061021100.png",
     * "repoId": "17448144-4b19-42f3-b564-205c57032f8f"
     * }
     * ],
     * "taskId": "56162BDA950411E78479446A2ED9DCBD"
     * }
     */
    private void addAttachmentFromRepo() {
        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("taskId", taskId);
        JsonArray pathInfoArray = new JsonArray();
        for (FolderDocumentEntity folderDocumentEntity : selectedFolderDocumentEntities) {
            if (folderDocumentEntity == null) continue;
            JsonObject pathInfo = new JsonObject();
            pathInfo.addProperty("filePath", String.format("%s%s", folderDocumentEntity.parent_dir, folderDocumentEntity.name));
            pathInfo.addProperty("repoId", folderDocumentEntity.repoId);
            pathInfoArray.add(pathInfo);
        }
        paramObject.add("pathInfoList", pathInfoArray);
        showLoadingDialog(R.string.str_uploading);
        callEnqueue(
                getApi().taskAddAttachmentFromRepo(RequestUtils.createJsonBody(paramObject.toString())),
                new SimpleCallBack<String>() {
                    @Override
                    public void onSuccess(Call<ResEntity<String>> call, Response<ResEntity<String>> response) {
                        dismissLoadingDialog();
                        showToast("上传成功");
                        dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResEntity<String>> call, Throwable t) {
                        dismissLoadingDialog();
                        super.onFailure(call, t);
                    }

                    @Override
                    public void defNotify(String noticeStr) {
                        super.defNotify(noticeStr);
                        showToast(noticeStr);
                    }
                });
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
                SeaFileSelectParam seaFileSelectParam = new SeaFileSelectParam(
                        iSeaFileSelectParams.getRepoType(),
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

            // selectedFolderDocumentEntities.addAll(iSeaFileSelectParams.getSelectedFolderDocuments());
            replaceFolderFragment(iSeaFileSelectParams);
        } else if (fragment instanceof RepoNavigationFragment && params != null) {
            selectRepoTypeEntity = (RepoTypeEntity) params.getSerializable(KEY_FRAGMENT_RESULT);
            replaceRepoListFragmemt();
        } else if (fragment instanceof RepoSelectListFragment && params != null) {
            RepoEntity repoEntity = (RepoEntity) params.getSerializable(KEY_FRAGMENT_RESULT);
            int repoType = params.getInt(KEY_REPO_TYPE);
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
        showPanel(false);
        dirPathTitleLayout.setVisibility(View.VISIBLE);
        this.iSeaFileSelectParams = null;
        foldrParentTv.setText(selectRepoTypeEntity.title);
        currFragment = addOrShowFragment(
                RepoSelectListFragment.newInstance(selectRepoTypeEntity.repoType, false),
                currFragment,
                R.id.main_fl_content);
    }

    /**
     * 替换文档目录视图
     */
    private void replaceFolderFragment(SeaFileSelectParam iSeaFileSelectParams) {
        showPanel(true);
        this.iSeaFileSelectParams = iSeaFileSelectParams;
        dirPathTitleLayout.setVisibility(View.VISIBLE);
        if (canBack2ParentDir()) {
            foldrParentTv.setText(getDstParentDirName());
        } else {
            foldrParentTv.setText(iSeaFileSelectParams.getRepoName());
        }
        currFragment = addOrShowFragment(
                SeaFileSelectFragment.newInstance(iSeaFileSelectParams),
                currFragment,
                R.id.main_fl_content);
    }


    /**
     * 更新选中的数量
     */
    private void updateSelectNum() {
        selectedNumTv.setText(
                getString(R.string.sfile_file_already_selected,
                        String.valueOf(selectedFolderDocumentAdapter.getItemCount()) + "/" + String.valueOf(10))
        );
        okTv.setEnabled(selectedFolderDocumentAdapter.getItemCount() > 0);
    }

    @Override
    public void onSeaFileSelect(FolderDocumentEntity folderDocumentEntity) {
        if (!selectedFolderDocumentEntities.contains(folderDocumentEntity)) {
            selectedFolderDocumentEntities.add(folderDocumentEntity);
        }
        selectedFolderDocumentAdapter.addItem(folderDocumentEntity);
    }

    @Override
    public void onSeaFileSelectCancel(FolderDocumentEntity folderDocumentEntity) {
        selectedFolderDocumentEntities.remove(folderDocumentEntity);
        selectedFolderDocumentAdapter.removeItem(folderDocumentEntity);
    }
}
