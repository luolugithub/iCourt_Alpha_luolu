package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.icourt.alpha.R;
import com.icourt.alpha.adapter.baseadapter.BaseFragmentAdapter;
import com.icourt.alpha.entity.bean.ProjectEntity;
import com.icourt.alpha.fragment.FileDirListFragment;
import com.icourt.alpha.fragment.GroupActionFragment;
import com.icourt.alpha.fragment.ProjectSaveListFragment;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.UriUtils;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

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
        implements OnFragmentCallBackListener, FragmentManager.OnBackStackChangedListener {

    Unbinder unbinder;
    @BindView(R.id.titleBack)
    ImageView titleBack;
    @BindView(R.id.titleContent)
    TextView titleContent;
    @BindView(R.id.titleAction)
    CheckedTextView titleAction;
    @BindView(R.id.main_fl_content)
    FrameLayout mainFlContent;
    Fragment currentFragment;

    SparseArray<CharSequence> titleArray = new SparseArray<>();

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
        View view = super.onCreateView(R.layout.dialog_fragment_share_project_file, inflater, container, savedInstanceState);
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
        getChildFragmentManager().addOnBackStackChangedListener(this);
        showFragment(ProjectSaveListFragment.newInstance());
    }

    @OnClick({R.id.titleBack, R.id.titleAction})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                if (!getChildFragmentManager().popBackStackImmediate()) {
                    dismiss();
                }
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

    private void showFragment(Fragment fragment) {
        currentFragment = addOrShowFragment(fragment, currentFragment, R.id.main_fl_content);
    }

    @Override
    protected Fragment addOrShowFragment(@NonNull Fragment targetFragment, Fragment currentFragment, @IdRes int containerViewId) {
        if (targetFragment == null) return currentFragment;
        if (targetFragment == currentFragment) return currentFragment;
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (fm.getBackStackEntryCount() > 0) {
            transaction.setCustomAnimations(
                    R.anim.fragment_slide_right_in, R.anim.fragment_slide_left_out,
                    R.anim.fragment_slide_left_in, R.anim.fragment_slide_right_out);
        }
        transaction.replace(containerViewId, targetFragment, String.valueOf(targetFragment.hashCode())).commitAllowingStateLoss();
        transaction.addToBackStack(String.valueOf(targetFragment.hashCode()));
        return targetFragment;
        // return super.addOrShowFragment(targetFragment, currentFragment, containerViewId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (fragment instanceof ProjectSaveListFragment && params != null) {
            Serializable serializable = params.getSerializable(KEY_FRAGMENT_RESULT);
            if (serializable instanceof ProjectEntity) {
                ProjectEntity projectEntity = (ProjectEntity) serializable;
                //1.保存标题
                titleArray.put(getChildFragmentManager().getBackStackEntryCount(), projectEntity.name);

                //2.替换
                showFragment(FileDirListFragment.newInstance(projectEntity.pkId, null, null, null, null));
            }
        } else if (fragment instanceof FileDirListFragment && params != null) {
            //文件夹嵌套
        }
    }

    @Override
    public void onBackStackChanged() {
        //3.更改标题
        titleContent.setText(titleArray.get(getChildFragmentManager().getBackStackEntryCount() - 1, "选择项目"));

        //4.保存按钮
    }
}
