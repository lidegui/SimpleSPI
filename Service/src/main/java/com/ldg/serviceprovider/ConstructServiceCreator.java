package com.ldg.serviceprovider;

/**
 * 利用类的限定名或者类对象构造实例
 * created by gui 2020/11/13
 */
public abstract class ConstructServiceCreator<T> implements ServiceCreator<T> {

    protected String mServiceImplName;
    protected Class<T> mServiceImplClass;
    protected boolean mIsSingleton;
    protected volatile T mServiceInstance;

    public ConstructServiceCreator(String serviceImplName, boolean isSingleton) {
        mServiceImplName = serviceImplName;
        mIsSingleton = isSingleton;
    }

    public ConstructServiceCreator(Class<T> serviceImplClass, boolean isSingleton) {
        mServiceImplClass = serviceImplClass;
        mIsSingleton = isSingleton;
    }

    @Override
    public T create(Object... constructArgs) {
        if (!mIsSingleton) {
            return getServiceImpl(constructArgs);
        }

        if (mServiceInstance != null) {
            return mServiceInstance;
        }

        synchronized (this) {
            if (mServiceInstance != null) {
                return mServiceInstance;
            }

            mServiceInstance = getServiceImpl(constructArgs);
        }

        return mServiceInstance;
    }

    private T getServiceImpl(Object[] constructArgs) {
        try {
            Class clazz;
            if (mServiceImplClass != null) {
                clazz = mServiceImplClass;
            } else {
                clazz = Class.forName(mServiceImplName);
            }
            return (T) newService(clazz, constructArgs);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    protected abstract T newService(Class<T> serviceImplClass, Object... constructArgs);
}
