package com.icourt.alpha.fragment;

import android.support.annotation.NonNull;

import com.icourt.alpha.base.BaseFragment;
import com.icourt.alpha.entity.bean.SFileTokenEntity;
import com.icourt.alpha.http.callback.SimpleCallBack2;

import retrofit2.Call;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/8/12
 * version 2.1.0
 */
public abstract class FolderBaseFragment extends BaseFragment {

    /**
     * 获取sfile token
     *
     * @param callBack2
     */
    protected Call<SFileTokenEntity<String>> getSFileToken(
            @NonNull SimpleCallBack2<SFileTokenEntity<String>> callBack2) {
        return callEnqueue(getApi().documentTokenQuery(), callBack2);
    }

}
