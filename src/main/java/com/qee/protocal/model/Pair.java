package com.qee.protocal.model;

import lombok.Data;

/**
 * Created by zhuqi on 2017/9/11.
 */
@Data
public class Pair<T, S> {

    private T first;

    private S second;

    public Pair() {
    }

    public Pair(T first, S second) {
        this.first = first;
        this.second = second;
    }
}
