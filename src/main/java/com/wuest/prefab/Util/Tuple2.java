package com.wuest.prefab.Util;

/**
 * A simple pair of values.
 *
 * @param <A> The type of the first value.
 * @param <B> The type of the second value.
 */
public class Tuple2<A, B> {

    public final A _1;
    public final B _2;

    public Tuple2(A first, B second) {
        this._1 = first;
        this._2 = second;
    }

    public A _1() {
        return this._1;
    }

    public B _2() {
        return this._2;
    }
}
