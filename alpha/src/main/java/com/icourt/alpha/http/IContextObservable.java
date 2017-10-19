package com.icourt.alpha.http;

import com.icourt.alpha.http.httpmodel.ResEntity;

import io.reactivex.Observable;

/**
 * Description  处理网络Observable
 * Company Beijing icourt
 * author  youxuan  E-mail:xuanyouwu@163.com
 * date createTime：2017/9/29
 * version 2.1.0
 */
public interface IContextObservable {


    /**
     * 发送Observable
     * 1.绑定生命周期
     * 2.分发常规模型 {@link ResEntity#succeed}
     *
     * @param observable
     * @param <T>
     * @return
     */
    <T> Observable<T> sendObservable(Observable<? extends ResEntity<T>> observable);

    /**
     * 发送Observable
     * 1.绑定生命周期
     * 2.分发常规模型 {@link ResEntity#succeed}
     *
     * @param observable
     * @param <T>
     * @return
     */
    <T> Observable<? extends ResEntity<T>> sendObservable2(Observable<? extends ResEntity<T>> observable);

    /**
     * 发送Observable
     * 1.绑定生命周期
     * 2.分发常规模型 {@link ResEntity#succeed}
     *
     * @param observable
     * @param <T>
     * @return
     */
    <T extends ResEntity> Observable<T> sendObservable3(Observable<T> observable);
}
