package com.ldg.serviceprovider;

/**
 * created by gui 2020/11/13
 */
public interface ServiceCreator<T> {
    public T create(Object... constructArgs);
}
