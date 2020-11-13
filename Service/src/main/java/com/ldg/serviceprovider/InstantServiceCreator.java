package com.ldg.serviceprovider;

/**
 * 直接提供服务实现类的实例，不经过反射
 * created by gui 2020/11/13
 */
public class InstantServiceCreator<T> implements ServiceCreator<T> {
    private T mInstance;

    public InstantServiceCreator(T instance) {
        mInstance = instance;
    }

    @Override
    public T create(Object... constructArgs) {
        return mInstance;
    }
}
