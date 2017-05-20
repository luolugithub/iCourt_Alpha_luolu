package com.icourt.alpha.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.FileUtils;

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

    public static FileImportNavFragment newInstance(String path) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_PATH, path);
        FileImportNavFragment importFilePathFragment = new FileImportNavFragment();
        importFilePathFragment.setArguments(bundle);
        return importFilePathFragment;
    }

    OnFragmentCallBackListener onFragmentCallBackListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onFragmentCallBackListener = (OnFragmentCallBackListener) context;
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
        String filePath = getArguments().getString(KEY_PATH);
        if (!TextUtils.isEmpty(filePath)) {
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
            tvFileName.setText(fileName);
            ivFileIcon.setImageResource(FileUtils.getFileIcon40(fileName));
            File file = new File(filePath);
            if (file.exists()) {
                tvFileSize.setText(String.format("(%s)", FileUtils.kbFromat(file.length())));
            }
        }
    }

    @OnClick({R.id.bt_path_friends,
            R.id.bt_send_program})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_path_friends:
                if (onFragmentCallBackListener != null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("page", 1);
                    onFragmentCallBackListener.onFragmentCallBack(this, 1, bundle);
                }
                break;
            case R.id.bt_send_program:
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
