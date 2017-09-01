package com.icourt.alpha.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSaveFileDialogFragment;
import com.icourt.alpha.interfaces.OnPageFragmentCallBack;
import com.icourt.alpha.utils.FileUtils;
import com.icourt.alpha.utils.GlideUtils;
import com.icourt.alpha.utils.IMUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Description  文件导入导航首页
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/20
 * version 1.0.0
 */
public class FileImportNavFragment extends BaseFragment {

    @BindView(R.id.file_type_img)
    ImageView fileTypeImg;
    @BindView(R.id.iv_file_icon)
    ImageView ivFileIcon;
    @BindView(R.id.tv_file_name)
    TextView tvFileName;
    @BindView(R.id.tv_file_size)
    TextView tvFileSize;
    @BindView(R.id.file_comm_type)
    LinearLayout fileCommType;
    @BindView(R.id.bt_path_friends)
    TextView btPathFriends;
    @BindView(R.id.bt_send_program)
    TextView btSendProgram;
    Unbinder unbinder;
    private static final String KEY_PATH = "path";
    private static final String KEY_DESC = "desc";
    String filePath;
    @BindView(R.id.send_program_ll)
    LinearLayout sendProgramLl;

    public static FileImportNavFragment newInstance(String path, String desc) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PATH, path);
        bundle.putString(KEY_DESC, desc);
        FileImportNavFragment importFilePathFragment = new FileImportNavFragment();
        importFilePathFragment.setArguments(bundle);
        return importFilePathFragment;
    }


    OnPageFragmentCallBack onPageFragmentCallBack;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onPageFragmentCallBack = (OnPageFragmentCallBack) context;
        } catch (ClassCastException e) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.fragment_file_import_nav, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        filePath = getArguments().getString(KEY_PATH);
        if (!TextUtils.isEmpty(filePath)) {
            if (filePath.startsWith("http")) {
                fileTypeImg.setVisibility(View.GONE);
                fileCommType.setVisibility(View.VISIBLE);
                sendProgramLl.setVisibility(View.GONE);
                tvFileName.setText(getArguments().getString(KEY_DESC, ""));
                tvFileSize.setText(filePath);
                ivFileIcon.setImageResource(R.mipmap.ic_share_url);
            } else if (IMUtils.isPIC(filePath)) {
                fileTypeImg.setVisibility(View.VISIBLE);
                fileCommType.setVisibility(View.GONE);
                sendProgramLl.setVisibility(View.VISIBLE);
                if (GlideUtils.canLoadImage(getContext())) {
                    Glide.with(getContext())
                            .load(filePath)
                            .into(fileTypeImg);
                }
            } else if (filePath.startsWith("content://")) {//fileProvider
                fileTypeImg.setVisibility(View.GONE);
                fileCommType.setVisibility(View.VISIBLE);
                sendProgramLl.setVisibility(View.VISIBLE);
                Cursor cursor = null;
                Uri fileUri;
                try {
                    fileUri = Uri.parse(filePath);
                    cursor = getContext().getContentResolver().query(fileUri, null, null, null, null);
                    cursor.moveToNext();
                    String fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    long fileSize = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
                    tvFileName.setText(fileName);
                    ivFileIcon.setImageResource(FileUtils.getFileIcon40(fileName));
                    tvFileSize.setText(String.format("(%s)", FileUtils.bFormat(fileSize)));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                fileTypeImg.setVisibility(View.GONE);
                fileCommType.setVisibility(View.VISIBLE);
                sendProgramLl.setVisibility(View.VISIBLE);
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
                tvFileName.setText(fileName);
                ivFileIcon.setImageResource(FileUtils.getFileIcon40(fileName));
                File file = new File(filePath);
                if (file.exists()) {
                    tvFileSize.setText(String.format("(%s)", FileUtils.bFormat(file.length())));
                }
            }
        }
    }

    @OnClick({R.id.bt_path_friends,
            R.id.bt_send_program})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_path_friends:
                if (onPageFragmentCallBack != null) {
                    onPageFragmentCallBack.onRequest2NextPage(this, 0, null);
                }
                break;
            case R.id.bt_send_program://分享到项目
//                ProjectSelectActivity.launch(getContext(), null, null, filePath);
                showProjectSaveFileDialogFragment(filePath);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 展示项目转发对话框
     *
     * @param filePath
     */
    public void showProjectSaveFileDialogFragment(String filePath) {
        String tag = ProjectSaveFileDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getChildFragmentManager().beginTransaction();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        mFragTransaction.add(ProjectSaveFileDialogFragment.newInstance(filePath, ProjectSaveFileDialogFragment.OTHER_TYPE), tag);
        mFragTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
