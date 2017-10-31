package com.icourt.alpha.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;

import com.google.gson.JsonElement;
import com.icourt.alpha.R;
import com.icourt.alpha.base.BaseActivity;
import com.icourt.alpha.constants.TimingConfig;
import com.icourt.alpha.entity.bean.AlphaUserInfo;
import com.icourt.alpha.fragment.dialogfragment.CalendaerSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.ProjectSimpleSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TaskSelectDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.TimingChangeDialogFragment;
import com.icourt.alpha.fragment.dialogfragment.WorkTypeSelectDialogFragment;
import com.icourt.alpha.http.callback.SimpleCallBack;
import com.icourt.alpha.http.httpmodel.ResEntity;
import com.icourt.alpha.utils.SystemUtils;
import com.icourt.alpha.widget.filter.LengthListenFilter;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/5/17
 * version 1.0.0
 */
public class BaseTimerActivity extends BaseActivity {
    //计时长度 输入提示
    protected final InputFilter[] timingNameInputFilters = LengthListenFilter.createSingleInputFilter(new LengthListenFilter(TimingConfig.TIMING_NAME_MAX_LENGTH) {
        @Override
        public void onInputOverLength(int maxLength) {
            showToast(getString(R.string.timing_name_limit_format, String.valueOf(maxLength)));
        }
    });
    //这个标记位是用来判断是否是进行了删除操作的标记位
    protected boolean isDelete = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 删除计时
     */
    protected final void deleteTiming(final String id) {
        new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("删除计时不可恢复")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLoadingDialog(null);
                        AlphaUserInfo loginUserInfo = getLoginUserInfo();
                        String clientId = "";
                        if (loginUserInfo != null) {
                            clientId = loginUserInfo.localUniqueId;
                        }
                        callEnqueue(getApi().timingDelete(id, clientId),
                                new SimpleCallBack<JsonElement>() {
                                    @Override
                                    public void onSuccess(Call<ResEntity<JsonElement>> call, Response<ResEntity<JsonElement>> response) {
                                        dismissLoadingDialog();
                                        isDelete = true;
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Call<ResEntity<JsonElement>> call, Throwable t) {
                                        super.onFailure(call, t);
                                        dismissLoadingDialog();
                                        isDelete = false;
                                    }
                                });
                    }
                }).setNegativeButton("取消", null)
                .show();
    }

    /**
     * 展示选择项目对话框
     */
    protected final void showProjectSelectDialogFragment(@Nullable String selectedProjectId) {
        SystemUtils.hideSoftKeyBoard(getActivity(), true);
        String tag = ProjectSimpleSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        ProjectSimpleSelectDialogFragment.newInstance(selectedProjectId)
                .show(mFragTransaction, tag);
    }

    /**
     * 展示选择工作类型对话框
     */
    protected final void showWorkTypeSelectDialogFragment(String projectId, String selectedWorkType) {
        String tag = WorkTypeSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        WorkTypeSelectDialogFragment.newInstance(projectId, selectedWorkType)
                .show(mFragTransaction, tag);
    }

    /**
     * 展示选择关联任务对话框
     */
    protected final void showTaskSelectDialogFragment(String projectId, String selectedTaskId) {
        SystemUtils.hideSoftKeyBoard(getActivity(), true);
        String tag = TaskSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        TaskSelectDialogFragment.newInstance(projectId, selectedTaskId)
                .show(mFragTransaction, tag);
    }

    /**
     * 展示选择工作类型对话框
     */
    protected final void showCalendaerSelectDialogFragment() {
        SystemUtils.hideSoftKeyBoard(getActivity(), true);
        String tag = CalendaerSelectDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        CalendaerSelectDialogFragment.newInstance()
                .show(mFragTransaction, tag);
    }

    /**
     * 显示选择时间的弹出窗
     *
     * @param type            所要修改的类型，开始时间？结束时间
     * @param timeStartMillis 传递进来的开始时间时间戳
     * @param timeEndMillis   传递进来的结束时间时间戳（如果是正在计时界面，这个值传0）
     * @return
     */
    protected void showDateTimeSelectDialogFragment(@TimingChangeDialogFragment.ChangeTimeType int type, long timeStartMillis, long timeEndMillis) {
        String tag = TimingChangeDialogFragment.class.getSimpleName();
        FragmentTransaction mFragTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            mFragTransaction.remove(fragment);
        }
        TimingChangeDialogFragment.newInstance(type, timeStartMillis, timeEndMillis).show(mFragTransaction, tag);
    }


}
