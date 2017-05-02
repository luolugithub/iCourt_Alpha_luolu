package com.icourt.alpha.view.emoji;

import android.Manifest;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.PhotosListAdapter;
import com.icourt.alpha.entity.bean.LocalImageEntity;
import com.icourt.alpha.utils.ImageUtils;
import com.icourt.alpha.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description  选择图片发送UI
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
public class MySelectPhotoLayout extends LinearLayout implements View.OnClickListener {


    public interface OnBottomChatPanelListener {
        /**
         * 多图选中 发送
         *
         * @param pics
         */
        boolean requestImageSend(List<String> pics);

        /**
         * 请求打开本地相册
         */
        void requestOpenPhotos();

        /**
         * 请求文件权限
         */
        void requestFilePermission();
    }

    public void setOnImageSendListener(OnBottomChatPanelListener onImageSendListener) {
        this.onImageSendListener = onImageSendListener;
    }

    OnBottomChatPanelListener onImageSendListener;
    LayoutInflater inflater;
    RecyclerView photoDispRecyclerView;
    TextView chatSelectFromAlbumBtn;
    TextView chatSendPhotoBtn;
    private View view;
    TextView btn_photo_file_permission;
    CheckBox cb_original_photo;
    PhotosListAdapter photosListAdapter;


    public MySelectPhotoLayout(Context context) {
        this(context, null);
    }


    public MySelectPhotoLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MySelectPhotoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        init();
    }

    protected void init() {
        view = inflater.inflate(R.layout.view_select_photo_send_layout, this);
        photoDispRecyclerView = (RecyclerView) view.findViewById(R.id.photoDispRecyclerView);
        chatSelectFromAlbumBtn = (TextView) view.findViewById(R.id.chat_select_from_album_btn);
        chatSelectFromAlbumBtn.setOnClickListener(this);
        chatSendPhotoBtn = (TextView) view.findViewById(R.id.chat_send_photo_btn);
        chatSendPhotoBtn.setOnClickListener(this);
        btn_photo_file_permission = (TextView) view.findViewById(R.id.btn_photo_file_permission);
        btn_photo_file_permission.setOnClickListener(this);
        cb_original_photo = (CheckBox) view.findViewById(R.id.cb_original_photo);

        photoDispRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        photoDispRecyclerView.setAdapter(photosListAdapter = new PhotosListAdapter());

        refreshFile();
    }

    /**
     * 是否拥有文件权限
     *
     * @return
     */
    private boolean hasFilePermission() {
        return SystemUtils.checkPermissions(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_select_from_album_btn:
                if (onImageSendListener != null) {
                    onImageSendListener.requestOpenPhotos();
                }
                break;
            case R.id.chat_send_photo_btn:
                if (onImageSendListener != null) {
                    if (cb_original_photo.isChecked()) {
                        ArrayList<LocalImageEntity> selectedData = photosListAdapter.getSelectedData();
                        List<String> photoList = new ArrayList<>();
                        for (int i = 0; i < selectedData.size(); i++) {
                            LocalImageEntity localImageEntity = selectedData.get(i);
                            if (localImageEntity != null) {
                                photoList.add(TextUtils.isEmpty(localImageEntity.photoPath)
                                        ? localImageEntity.thumbPath : localImageEntity.photoPath);
                            }
                        }
                        //原图
                        if (onImageSendListener.requestImageSend(photoList)) {
                            photosListAdapter.clearSelected();
                            cb_original_photo.setChecked(false);
                        }
                    } else {
                        ArrayList<LocalImageEntity> selectedData = photosListAdapter.getSelectedData();
                        List<String> photoList = new ArrayList<>();
                        for (int i = 0; i < selectedData.size(); i++) {
                            LocalImageEntity localImageEntity = selectedData.get(i);
                            if (localImageEntity != null) {
                                photoList.add(TextUtils.isEmpty(localImageEntity.thumbPath)
                                        ? localImageEntity.photoPath : localImageEntity.thumbPath);
                            }
                        }
                        //缩略图
                        if (onImageSendListener.requestImageSend(photoList)) {
                            photosListAdapter.clearSelected();
                            cb_original_photo.setChecked(false);
                        }
                    }
                }
                break;
            case R.id.btn_photo_file_permission:
                if (onImageSendListener != null) {
                    onImageSendListener.requestFilePermission();
                }
                break;
        }
    }

    /**
     * 刷新图片
     */
    public void refreshFile() {
        if (hasFilePermission()) {
            btn_photo_file_permission.setVisibility(GONE);
            photosListAdapter.clearSelected();
            loadPhotosFromSD();
        } else {
            btn_photo_file_permission.setVisibility(VISIBLE);
        }
    }

    private Thread loadPhotosThread;
    private Handler mHandler = new Handler();

    /**
     * 从本地获取图片
     */
    private void loadPhotosFromSD() {
        loadPhotosThread = new Thread() {
            @Override
            public void run() {
                if (!isInterrupted()) {
                    if (mHandler != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (photosListAdapter != null) {
                                    photosListAdapter.bindData(true, ImageUtils.getAllPhotoFolder(getContext()));
                                }
                            }
                        });
                    }
                }
            }
        };
        loadPhotosThread.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (loadPhotosThread != null) {
            try {
                loadPhotosThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
