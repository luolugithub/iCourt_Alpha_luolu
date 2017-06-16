package com.icourt.alpha.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.activity.FileBoxDownloadActivity;
import com.icourt.alpha.adapter.TaskAttachmentAdapter;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.TaskAttachmentEntity;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.interfaces.OnUpdateTaskListener;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.ItemDecorationUtils;
import com.icourt.alpha.utils.LogUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.api.RequestUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Description  任务附件列表
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/12
 * version 2.0.0
 */

public class TaskAttachmentFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener {
    private static final String KEY_TASK_ID = "key_task_id";
    private static final String KEY_HAS_PERMISSION = "key_has_permission";
    private static final int REQUEST_CODE_CAMERA = 1000;
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_CODE_AT_MEMBER = 1002;

    private static final int REQ_CODE_PERMISSION_CAMERA = 1100;
    private static final int REQ_CODE_PERMISSION_ACCESS_FILE = 1101;

    Unbinder unbinder;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.add_attachment_view)
    TextView addAttachmentView;

    String taskId;
    String path;
    TaskAttachmentAdapter taskAttachmentAdapter;
    OnUpdateTaskListener updateTaskListener;
    boolean hasPermission;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.list_layout)
    LinearLayout listLayout;

    public static TaskAttachmentFragment newInstance(@NonNull String taskId, boolean hasPermission) {
        TaskAttachmentFragment taskAttachmentFragment = new TaskAttachmentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TASK_ID, taskId);
        bundle.putBoolean(KEY_HAS_PERMISSION, hasPermission);
        taskAttachmentFragment.setArguments(bundle);
        return taskAttachmentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_task_attachment_layout, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            updateTaskListener = (OnUpdateTaskListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initView() {
        taskId = getArguments().getString(KEY_TASK_ID);
        hasPermission = getArguments().getBoolean(KEY_HAS_PERMISSION);
        recyclerview.setNestedScrollingEnabled(false);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true, R.color.alpha_divider_color));
        recyclerview.setAdapter(taskAttachmentAdapter = new TaskAttachmentAdapter());
        taskAttachmentAdapter.setOnItemClickListener(this);
        taskAttachmentAdapter.setOnItemLongClickListener(this);
        getData(true);
        addAttachmentView.setVisibility(hasPermission ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.add_attachment_view})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.add_attachment_view://添加附件
                if (hasPermission) {
                    showBottomAddMeau();
                } else {
                    showTopSnackBar("您没有编辑任务的权限");
                }
                break;
        }
    }

    /**
     * 打开相机
     */
    private void checkAndOpenCamera() {
        if (checkPermission(Manifest.permission.CAMERA)) {
            path = SystemUtils.getFileDiskCache(getContext()) + File.separator
                    + System.currentTimeMillis() + ".png";
            Uri picUri = Uri.fromFile(new File(path));
            SystemUtils.doTakePhotoAction(this, picUri, REQUEST_CODE_CAMERA);
        } else {
            reqPermission(Manifest.permission.CAMERA, "我们需要拍照权限!", REQ_CODE_PERMISSION_CAMERA);
        }
    }

    /**
     * 打开相册
     */
    private void checkAndOpenPhotos() {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            FunctionConfig config = new FunctionConfig.Builder()
                    .setMutiSelectMaxSize(9)
                    .build();
            GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, config, mOnHanlderResultCallback);
        } else {

            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件读写权限!", REQ_CODE_PERMISSION_ACCESS_FILE);
        }
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                for (PhotoInfo photoInfo : resultList) {
                    if (photoInfo != null && !TextUtils.isEmpty(photoInfo.getPhotoPath())) {
                        uploadAttachmentToTask(photoInfo.getPhotoPath());
                    }
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };

    /**
     * 显示底部添加菜单
     */
    private void showBottomAddMeau() {
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList("拍照", "从手机相册选择"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                checkAndOpenCamera();
                                break;
                            case 1:
                                checkAndOpenPhotos();
                                break;
                        }
                    }
                }).show();
    }

    /**
     * 显示底部删除菜单
     */
    private void showBottomDeleteMeau(final TaskAttachmentEntity entity) {
        new BottomActionDialog(getContext(),
                null,
                Arrays.asList("删除"),
                new BottomActionDialog.OnActionItemClickListener() {
                    @Override
                    public void onItemClick(BottomActionDialog dialog, BottomActionDialog.ActionItemAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                deleteAttachment(entity);
                                break;
                        }
                    }


                }).show();
    }

    private void updateDocument() {
        if (getParentFragment() instanceof OnFragmentCallBackListener) {
            updateTaskListener = (OnUpdateTaskListener) getParentFragment();
        }
        if (updateTaskListener != null) {
            updateTaskListener.onUpdateDocument(String.valueOf(taskAttachmentAdapter.getItemCount()));
        }
    }

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getApi().taskAttachMentListQuery(taskId).enqueue(new SimpleCallBack<List<TaskAttachmentEntity>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<TaskAttachmentEntity>>> call, Response<ResEntity<List<TaskAttachmentEntity>>> response) {
                if (response.body().result != null) {
                    taskAttachmentAdapter.bindData(true, response.body().result);
                    if (response.body().result.size() <= 0) {
                        if (!hasPermission) {
                            listLayout.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                        } else {
                            listLayout.setVisibility(View.VISIBLE);
                            emptyLayout.setVisibility(View.GONE);
                        }
                    }
                } else {
                    listLayout.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                }
                updateDocument();
            }
        });
    }

    /**
     * 上传任务附件
     *
     * @param filePath
     */
    private void uploadAttachmentToTask(String filePath) {
        LogUtils.e("filePath :   " + filePath);
        if (TextUtils.isEmpty(filePath)) return;
        File file = new File(filePath);
        if (!file.exists()) {
            showTopSnackBar("文件不存在啦");
            return;
        }
        showLoadingDialog("正在上传...");
        String key = "file\";filename=\"" + DateUtils.millis() + ".png";
        Map<String, RequestBody> params = new HashMap<>();
        params.put(key, RequestUtils.createImgBody(file));
        getApi().taskAttachmentUpload(taskId, params).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                showTopSnackBar("上传成功");
                getData(true);
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
                showTopSnackBar("上传失败");
            }
        });
    }

    /**
     * 删除任务附件
     *
     * @param entity
     */
    private void deleteAttachment(final TaskAttachmentEntity entity) {
        if (entity.pathInfoVo == null) return;
        showLoadingDialog(null);
        getApi().taskDocumentDelete(taskId, entity.pathInfoVo.filePath).enqueue(new SimpleCallBack<JsonElement>() {
            @Override
            public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                dismissLoadingDialog();
                if (taskAttachmentAdapter != null)
                    taskAttachmentAdapter.removeItem(entity);
            }

            @Override
            public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                super.onFailure(call, t);
                dismissLoadingDialog();
                showTopSnackBar("删除失败");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    uploadAttachmentToTask(path);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (hasPermission) {
            TaskAttachmentEntity entity = (TaskAttachmentEntity) adapter.getItem(position);
            if (entity.pathInfoVo != null)
                FileBoxDownloadActivity.launch(getContext(), null, entity.pathInfoVo.repoId, entity.pathInfoVo.filePath, FileBoxDownloadActivity.TASK_DOWNLOAD_FILE_ACTION);
        }
    }

    @Override
    public boolean onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        if (hasPermission) {
            TaskAttachmentEntity entity = (TaskAttachmentEntity) adapter.getItem(position);
            if (entity.pathInfoVo != null)
                showBottomDeleteMeau(entity);
        }
        return false;
    }
}
