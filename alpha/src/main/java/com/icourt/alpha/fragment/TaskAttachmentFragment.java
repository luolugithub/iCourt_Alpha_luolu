package com.icourt.alpha.fragment;

import android.Manifest;
import android.app.Activity;
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
import android.view.WindowManager;
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

public class TaskAttachmentFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener {
    private static final String KEY_TASK_ID = "key_task_id";

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

    public static TaskAttachmentFragment newInstance(@NonNull String taskId) {
        TaskAttachmentFragment taskAttachmentFragment = new TaskAttachmentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TASK_ID, taskId);
        taskAttachmentFragment.setArguments(bundle);
        return taskAttachmentFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_task_attachment_layout, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        taskId = getArguments().getString(KEY_TASK_ID);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.addItemDecoration(ItemDecorationUtils.getCommFull05Divider(getContext(), true));
        recyclerview.setAdapter(taskAttachmentAdapter = new TaskAttachmentAdapter());
        taskAttachmentAdapter.setOnItemClickListener(this);
        getData(true);
    }

    @OnClick({R.id.add_attachment_view})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.add_attachment_view://添加附件
                showBottomMeau();
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
     * 显示底部菜单
     */
    private void showBottomMeau() {
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

    @Override
    protected void getData(boolean isRefresh) {
        super.getData(isRefresh);
        getApi().taskAttachMentListQuery(taskId).enqueue(new SimpleCallBack<List<TaskAttachmentEntity>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<TaskAttachmentEntity>>> call, Response<ResEntity<List<TaskAttachmentEntity>>> response) {
                taskAttachmentAdapter.bindData(true, response.body().result);
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.ViewHolder holder, View view, int position) {
        TaskAttachmentEntity entity = (TaskAttachmentEntity) adapter.getItem(position);
        if (entity.pathInfoVo != null)
            FileBoxDownloadActivity.launch(getContext(), null, entity.pathInfoVo.repoId, entity.pathInfoVo.filePath, FileBoxDownloadActivity.TASK_DOWNLOAD_FILE_ACTION);
    }
}
