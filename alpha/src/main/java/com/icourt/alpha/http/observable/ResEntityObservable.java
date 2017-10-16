package com.icourt.alpha.http.observable;

import com.icourt.alpha.http.exception.ResponseException;
import com.icourt.alpha.http.httpmodel.ResEntity;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Description
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/29
 * version 2.1.0
 */
public abstract class ResEntityObservable<T> extends Observable<ResEntity<T>> {
    public ResEntityObservable() {
        map(new Function<ResEntity<T>, ResEntity<T>>() {
            @Override
            public ResEntity<T> apply(@NonNull ResEntity<T> tResEntity) throws Exception {
                if (!tResEntity.succeed) {
                    throw new ResponseException(-1, tResEntity.message);
                }
                return tResEntity;
            }
        });
    }
}
