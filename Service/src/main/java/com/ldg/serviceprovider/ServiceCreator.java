package com.ldg.serviceprovider;

/**
 * created by gui 2020/11/13
 */
public interface ServiceCreator<T> {
    T create(Object... constructArgs);
}
