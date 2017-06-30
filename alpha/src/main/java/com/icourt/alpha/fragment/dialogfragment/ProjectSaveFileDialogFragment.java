package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.fragment.GroupActionFragment;
import com.icourt.alpha.fragment.ProjectSaveListFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.UriUtils;

import java.io.File;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Description  文件保存到项目
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/10
 * version 1.0.0
 */
public class ProjectSaveFileDialogFragment extends BaseDialogFragment
        implements OnFragmentCallBackListener {

    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    Unbinder unbinder;
    BaseFragmentAdapter baseFragmentAdapter;
    @BindView(R.id.titleContent)
    TextView titleContent;

    /**
     * 文档转发到项目
     *
     * @param filePath
     * @return
     */
    public static ProjectSaveFileDialogFragment newInstance(@NonNull String filePath) {
        ProjectSaveFileDialogFragment contactDialogFragment = new ProjectSaveFileDialogFragment();
        Bundle args = new Bundle();
        args.putString("filePath", filePath);
        contactDialogFragment.setArguments(args);
        return contactDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(R.layout.dialog_fragment_share, inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 20);
                    decorView.setPadding(dp20 / 2, dp20, dp20 / 2, dp20);
                }
            }
        }
        titleAction.setVisibility(View.INVISIBLE);
        titleContent.setText("选择项目");
        viewPager.setAdapter(baseFragmentAdapter = new BaseFragmentAdapter(getChildFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                titleAction.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                titleContent.setText(position == 0 ? "选择项目" : "文件夹");
            }
        });
        baseFragmentAdapter.bindData(true, Arrays.asList(ProjectSaveListFragment.newInstance(), GroupActionFragment.newInstance()));
    }

    @OnClick({R.id.titleBack, R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                if (viewPager.getCurrentItem() > 0) {
                    viewPager.setCurrentItem(0);
                    return;
                }
                dismiss();
                break;
            case R.id.titleAction:

                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 保存文件到项目
     */
    private void shareFile2Project(String path) {
        if (TextUtils.isEmpty(path)) return;
        ParcelFileDescriptor n_fileDescriptor = null;
        File file = null;
        Uri fileUri = null;
        if (path.startsWith("content://")) {
            try {
                fileUri = Uri.parse(path);
                n_fileDescriptor = UriUtils.get_N_FileDescriptor(getContext(), fileUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (n_fileDescriptor == null) {
                showTopSnackBar("共享文件不存在啦");
                return;
            }
        } else {
            file = new File(path);
            if (!file.exists()) {
                showTopSnackBar("文件不存在啦");
                return;
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment == baseFragmentAdapter.getItem(0)) {
            viewPager.setCurrentItem(1);
        }
    }
}
