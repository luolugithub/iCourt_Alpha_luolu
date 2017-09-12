package com.icourt.alpha.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseRecyclerAdapter;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.utils.DateUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.widget.dialog.BottomActionDialog;
import com.icourt.api.RequestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

import static com.icourt.alpha.activity.UpdateUserInfoActivity.UPDATE_EMAIL_TYPE;
import static com.icourt.alpha.activity.UpdateUserInfoActivity.UPDATE_NAME_TYPE;
import static com.icourt.alpha.activity.UpdateUserInfoActivity.UPDATE_PHONE_TYPE;

/**
 * Description 设置
 * Company Beijing icourt
 * author  lu.zhao  E-mail:zhaolu@icourt.cc
 * date createTime：17/5/4
 * version 2.0.0
 */

public class UserInfoActivity extends BaseActivity {
    private static final int REQUEST_CODE_CAMERA = 1000;
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_CODE_CROP = 1002;
    private static final int REQ_CODE_PERMISSION_CAMERA = 1100;
    private static final int REQ_CODE_PERMISSION_ACCESS_FILE = 1101;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleView)
    AppBarLayout titleView;
    @BindView(R.id.photo_image)
    ImageView photoImage;
    @BindView(R.id.photo_layout)
    LinearLayout photoLayout;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.name_layout)
    LinearLayout nameLayout;
    @BindView(R.id.phone_tv)
    TextView phoneTv;
    @BindView(R.id.phone_layout)
    LinearLayout phoneLayout;
    @BindView(R.id.email_tv)
    TextView emailTv;
    @BindView(R.id.email_layout)
    LinearLayout emailLayout;

    private AlphaUserInfo alphaUserInfo;
    private String path;

    public static void launch(@NonNull Context context) {
        if (context == null) return;
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("个人设置");
        alphaUserInfo = getLoginUserInfo();
        if (alphaUserInfo != null) {
            GlideUtils.loadUser(this, alphaUserInfo.getPic(), photoImage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDataToView();
    }

    private void setDataToView() {
        alphaUserInfo = getLoginUserInfo();
        nameTv.setText(alphaUserInfo.getName());
        phoneTv.setText(alphaUserInfo.getPhone());
        emailTv.setText(alphaUserInfo.getMail());

    }

    @OnClick({R.id.photo_layout,
            R.id.name_layout,
            R.id.phone_layout,
            R.id.email_layout})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.photo_layout:
                showBottomAddMeau();
                break;
            case R.id.name_layout:
                UpdateUserInfoActivity.launch(this, UPDATE_NAME_TYPE, alphaUserInfo.getName());
                break;
            case R.id.phone_layout:
                UpdateUserInfoActivity.launch(this, UPDATE_PHONE_TYPE, alphaUserInfo.getPhone());
                break;
            case R.id.email_layout:
                UpdateUserInfoActivity.launch(this, UPDATE_EMAIL_TYPE, alphaUserInfo.getMail());
                break;
        }
    }

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
     * 打开相机
     */
    private void checkAndOpenCamera() {
        if (checkPermission(Manifest.permission.CAMERA)) {
            path = SystemUtils.getFileDiskCache(getContext()) + File.separator
                    + System.currentTimeMillis() + ".png";
            Uri picUri = Uri.fromFile(new File(path));
            FunctionConfig config = new FunctionConfig.Builder()
                    .setEnableCrop(true)
                    .build();
            GalleryFinal.openCamera(REQUEST_CODE_CAMERA, config, mOnHanlderResultCallback);
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
                    .setEnableCrop(true)
                    .setForceCrop(true)
                    .setForceCropEdit(false)
                    .setCropSquare(true)
                    .build();
            GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, config, mOnHanlderResultCallback);
        } else {
            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件读写权限!", REQ_CODE_PERMISSION_ACCESS_FILE);
        }
    }

    private void cropPhoto(String photoPath) {
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            FunctionConfig config = new FunctionConfig.Builder()
                    .setEnableCrop(true)
                    .setCropSquare(true)
                    .setCropWidth(700)
                    .setCropHeight(700)
                    .build();
            GalleryFinal.openCrop(REQUEST_CODE_CROP, config, photoPath, mOnHanlderResultCallback);
        } else {
            reqPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "我们需要文件读写权限!", REQ_CODE_PERMISSION_ACCESS_FILE);
        }
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null && resultList.size() > 0) {
                String photoPath = resultList.get(0).getPhotoPath();
                if (!TextUtils.isEmpty(photoPath) && new File(photoPath).exists()) {
                    switch (reqeustCode) {
                        case REQUEST_CODE_CAMERA:
                        case REQUEST_CODE_GALLERY:
                            cropPhoto(resultList.get(0).getPhotoPath());
                            break;
                        case REQUEST_CODE_CROP:
                            if (resultList.size() > 0) {
                                GlideUtils.loadUser(UserInfoActivity.this, resultList.get(0).getPhotoPath(), photoImage);
                            }
                            break;
                    }
                } else {
                    showToast("图片不存在");
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {

        }
    };

    /**
     * 批量上传文件
     *
     * @param filePaths 文件路径
     */
    private void uploadFiles(final String taskId, final List<String> filePaths) {
        if (TextUtils.isEmpty(taskId)) return;
        if (filePaths == null && filePaths.isEmpty()) return;
        showLoadingDialog("正在上传...");
        Observable.just(filePaths)
                .flatMap(new Function<List<String>, ObservableSource<JsonElement>>() {
                    @Override
                    public ObservableSource<JsonElement> apply(@io.reactivex.annotations.NonNull List<String> strings) throws Exception {
                        List<Observable<JsonElement>> observables = new ArrayList<Observable<JsonElement>>();
                        for (int i = 0; i < strings.size(); i++) {
                            String filePath = strings.get(i);
                            if (TextUtils.isEmpty(filePath)) {
                                continue;
                            }
                            File file = new File(filePath);
                            if (!file.exists()) {
                                continue;
                            }
                            String key = "file\";filename=\"" + DateUtils.millis() + ".png";
                            Map<String, RequestBody> params = new HashMap<>();
                            params.put(key, RequestUtils.createImgBody(file));
                            observables.add(getApi().taskAttachmentUploadObservable(taskId, params));
                        }
                        return Observable.concat(observables);
                    }
                })
                .compose(this.<JsonElement>bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonElement>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable disposable) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JsonElement jsonElement) {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable throwable) {
                        dismissLoadingDialog();
                        showTopSnackBar("更新失败");
                    }

                    @Override
                    public void onComplete() {
                        dismissLoadingDialog();
                        showTopSnackBar("更新成功");
                    }
                });
    }
}
