package com.icourt.alpha.view.emoji;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.PhotosListAdapter;

/**
 * Description  选择图片发送UI
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/4/24
 * version 1.0.0
 */
public class MySelectPhotoLayout extends LinearLayout {

    LayoutInflater inflater;
    RecyclerView photoDispRecyclerView;
    TextView chatSelectFromAlbumBtn;
    TextView chatSendPhotoBtn;
    private View view;
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
        chatSendPhotoBtn = (TextView) view.findViewById(R.id.chat_send_photo_btn);

        photoDispRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        photoDispRecyclerView.setAdapter(photosListAdapter = new PhotosListAdapter());
    }

}
