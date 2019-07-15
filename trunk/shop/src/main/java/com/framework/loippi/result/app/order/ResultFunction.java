package com.framework.loippi.result.app.order;

/**
 * Created by Administrator on 2017/12/12.
 */
public interface ResultFunction<T, E> {

    T callback(E result);
}
