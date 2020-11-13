package com.ldg.serviceprovider;

/**
 * 无参数构造方法
 * created by gui 2020/11/13
 */
public class DefaultConstructServiceCreator<T> extends ConstructServiceCreator<T> {

    public DefaultConstructServiceCreator(String serviceImplName, boolean isSingleton) {
        super(serviceImplName, isSingleton);
    }

    public DefaultConstructServiceCreator(Class<T> serviceImplClass, boolean isSingleton) {
        super(serviceImplClass, isSingleton);
    }

    @Override
    protected T newService(Class<T> serviceImplClass, Object... constructArgs) {
        try {
            return serviceImplClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
}
