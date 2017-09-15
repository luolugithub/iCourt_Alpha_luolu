package com.icourt.alpha.fragment.dialogfragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseDialogFragment;
import com.icourt.alpha.entity.bean.RepoIdResEntity;
import com.icourt.alpha.fragment.FileDirListFragment;
import com.icourt.alpha.fragment.ProjectSaveListFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.callback.SimpleCallBack2;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.interfaces.OnFragmentCallBackListener;
import com.icourt.alpha.utils.DensityUtil;
import com.icourt.alpha.utils.UriUtils;
import com.icourt.api.RequestUtils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Description  文件保存到项目
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/10
 * version 1.0.0
 */
public class ProjectSaveFileDialogFragment extends BaseDialogFragment
        implements OnFragmentCallBackListener, FragmentManager.OnBackStackChangedListener {

    public static final int OTHER_TYPE = 1;//外部保存
    public static final int ALPHA_TYPE = 2;//内部保存

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
    String projectId, seaFileRepoId, filePath, rootName;
    @BindView(R.id.bt_cancel)
    TextView btCancel;

    @IntDef({OTHER_TYPE,
            ALPHA_TYPE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SAVE_TYPE {

    }

    int type;

    /**
     * 文档转发到项目
     *
     * @param filePath
     * @return
     */
    public static ProjectSaveFileDialogFragment newInstance(@NonNull String filePath, @SAVE_TYPE int type) {
        ProjectSaveFileDialogFragment contactDialogFragment = new ProjectSaveFileDialogFragment();
        Bundle args = new Bundle();
        args.putString("filePath", filePath);
        args.putInt("type", type);
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
        type = getArguments().getInt("type");
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);
                if (type == OTHER_TYPE) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                }
                View decorView = window.getDecorView();
                if (decorView != null) {
                    int dp20 = DensityUtil.dip2px(getContext(), 30);
                    decorView.setPadding(dp20, 0, dp20, 0);
                }
            }
        }
        filePath = getArguments().getString("filePath");
        titleAction.setVisibility(View.INVISIBLE);
        titleContent.setText("选择项目");
        titleAction.setText("保存");
        getChildFragmentManager().addOnBackStackChangedListener(this);
        showFragment(ProjectSaveListFragment.newInstance());
    }

    @OnClick({R.id.titleBack, R.id.titleAction, R.id.bt_cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titleBack:
                if (getChildFragmentManager().getBackStackEntryCount() > 1) {
                    getChildFragmentManager().popBackStack();
                } else {
                    dismiss();
                }
                break;
            case R.id.bt_cancel:
                dismiss();
                break;
            case R.id.titleAction:
                if (TextUtils.isEmpty(seaFileRepoId)) {
                    getDocumentId();
                } else {
                    shareFile2Project(filePath, seaFileRepoId, rootName);
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    /**
     * 获取根目录id
     */
    private void getDocumentId() {
        callEnqueue(getApi().projectQueryDocumentId(projectId), new SimpleCallBack2<RepoIdResEntity>() {
            @Override
            public void onSuccess(Call<RepoIdResEntity> call, Response<RepoIdResEntity> response) {
                if (!TextUtils.isEmpty(response.body().seaFileRepoId)) {
                    seaFileRepoId = response.body().seaFileRepoId;
                    shareFile2Project(filePath, seaFileRepoId, rootName);
                } else {
                    bugSync("项目repo 获取null", "projectid:" + projectId);
                    showTopSnackBar("seaFileRepoId 返回null");
                }
            }
        });
    }

    /**
     * 保存文件到项目
     */
    private void shareFile2Project(String filePath, String seaFileRepoId, String rootName) {
        if (TextUtils.isEmpty(filePath)) return;
        ParcelFileDescriptor n_fileDescriptor = null;
        File file = null;
        Uri fileUri = null;
        if (filePath.startsWith("content://")) {
            try {
                fileUri = Uri.parse(filePath);
                n_fileDescriptor = UriUtils.get_N_FileDescriptor(getContext(), fileUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (n_fileDescriptor == null) {
                showTopSnackBar("共享文件不存在啦");
                return;
            }
        } else {
            file = new File(filePath);
            if (!file.exists()) {
                showTopSnackBar("文件不存在啦");
                return;
            }
        }
        getUploadUrl(filePath, seaFileRepoId, rootName);
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

    /**
     * 获取项目权限
     */
    private void checkAddTaskAndDocumentPms(String projectId) {
        getApi().permissionQuery(getLoginUserId(), "MAT", projectId).enqueue(new SimpleCallBack<List<String>>() {
            @Override
            public void onSuccess(Call<ResEntity<List<String>>> call, Response<ResEntity<List<String>>> response) {
                if (response.body().result != null) {
                    if (response.body().result.contains("MAT:matter.document:readwrite")) {
                        showOrHiddenSaveBtn(true);
                    } else {
                        showOrHiddenSaveBtn(false);
                    }
                } else {
                    showOrHiddenSaveBtn(false);
                }
            }

            @Override
            public void onFailure(Call<ResEntity<List<String>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    /**
     * 隐藏显示保存按钮
     *
     * @param isShow
     */
    private void showOrHiddenSaveBtn(boolean isShow) {
        titleAction.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onFragmentCallBack(Fragment fragment, int type, Bundle params) {
        if (params == null) return;
        projectId = params.getString("projectId");
        seaFileRepoId = params.getString("seaFileRepoId");
        log("projectId --- " + projectId);
        log("seaFileRepoId --- " + seaFileRepoId);
        if (TextUtils.isEmpty(projectId)) return;
        checkAddTaskAndDocumentPms(projectId);
        titleBack.setVisibility(View.VISIBLE);
        if (fragment instanceof ProjectSaveListFragment) {
            String projectName = params.getString("projectName");
            log("projectName --- " + projectName);

            //1.保存标题
            titleArray.put(getChildFragmentManager().getBackStackEntryCount(), projectName);

            //2.替换
            showFragment(FileDirListFragment.newInstance(projectId, filePath, null, null));

        } else if (fragment instanceof FileDirListFragment) {
            //文件夹嵌套
            String dirName = params.getString("dirName");
            rootName = params.getString("rootName");
            log("dirName --- " + dirName);
            log("rootName --- " + rootName);

            if (TextUtils.isEmpty(rootName)) return;
            //1.保存标题
            titleArray.put(getChildFragmentManager().getBackStackEntryCount(), dirName);

            //2.替换
            showFragment(FileDirListFragment.newInstance(projectId, filePath, rootName, seaFileRepoId));
        }
    }

    @Override
    public void onBackStackChanged() {
        //3.更改标题
        titleContent.setText(titleArray.get(getChildFragmentManager().getBackStackEntryCount() - 1, "选择项目"));
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            titleBack.setVisibility(View.VISIBLE);
            showOrHiddenSaveBtn(true);
        } else {
            titleBack.setVisibility(View.INVISIBLE);
            showOrHiddenSaveBtn(false);
        }

    }

    /**
     * 获取上传文件url
     *
     * @param filePath
     */
    private void getUploadUrl(final String filePath, String seaFileRepoId, final String rootName) {
        if (TextUtils.isEmpty(filePath)) return;
        File file = new File(filePath);
        if (!file.exists()) {
            showTopSnackBar("文件不存在啦");
            return;
        }
        showLoadingDialog("正在上传...");
        getSFileApi().projectUploadUrlQuery(seaFileRepoId).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.body() != null) {
                    String uploadUrl = response.body().getAsString();
                    uploadFile(uploadUrl, filePath, rootName);
                } else {
                    dismissLoadingDialog();
                    showTopSnackBar("上传失败");
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                dismissLoadingDialog();
                showTopSnackBar("上传失败");
            }

        });
    }


    /**
     * 上传文件
     *
     * @param uploadUrl
     * @param filePath
     */
    private void uploadFile(String uploadUrl, String filePath, String rootName) {
        if (TextUtils.isEmpty(filePath)) return;
        File file = new File(filePath);
        String fileName = file.getName();
        String key = "file\";filename=\"" + fileName;
        Map<String, RequestBody> params = new HashMap<>();
        params.put("parent_dir", TextUtils.isEmpty(rootName) ? RequestUtils.createTextBody("/") : RequestUtils.createTextBody(rootName));
        params.put(key, RequestUtils.createStreamBody(file));
        getSFileApi().sfileUploadFile(uploadUrl, params).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                dismissLoadingDialog();
                showTopSnackBar("上传成功");
                dismiss();
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                dismissLoadingDialog();
                showTopSnackBar("上传失败");
            }
        });
    }

}
