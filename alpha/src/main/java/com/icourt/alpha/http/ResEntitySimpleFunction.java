package com.icourt.alpha.http;

import android.text.TextUtils;

import com.icourt.alpha.http.exception.ResponseException;
import com.icourt.alpha.http.httpmodel.ResEntity;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;


/**
 * Description  向下分发网络错误
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/29
 * version 2.1.0
 */
public class ResEntitySimpleFunction<T> implements Function<ResEntity<T>, T> {
    @Override
    public T apply(@NonNull ResEntity<T> tResEntity) throws Exception {
        if (!tResEntity.succeed) {
            throw new ResponseException(-2, TextUtils.isEmpty(tResEntity.message)
                    ? "succeed=false;message=null" : tResEntity.message);
        }
        return tResEntity.result;
    }
}
