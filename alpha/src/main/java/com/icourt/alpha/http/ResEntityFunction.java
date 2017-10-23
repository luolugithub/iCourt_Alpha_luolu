package com.icourt.alpha.http;

import android.text.TextUtils;

import com.icourt.alpha.http.exception.ResponseException;
import com.icourt.alpha.http.httpmodel.ResEntity;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;


/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/29
 * version 2.1.0
 */
public class ResEntityFunction<T extends ResEntity> implements Function<T, T> {
    @Override
    public T apply(@NonNull T t) throws Exception {
        if (!t.succeed) {
            throw new ResponseException(-2, TextUtils.isEmpty(t.message)
                    ? "succeed=false;message=null" : t.message);
        }
        return t;
    }
}
