package com.ldg.serviceprovider;

import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务管理类，注册和获取服务
 * created by gui 2020/11/13
 */
public class ServiceManager {
    private static final Map<Object, ServiceCreator> SERVICE_CLASS_MAP = new ConcurrentHashMap<>();

    private static void checkNotNull(Object... args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("ServiceManager:the params is null");
        }

        for (Object arg : args) {
            if (arg == null
                    || String.class.isInstance(arg) && TextUtils.isEmpty((String) arg)) {
                throw new IllegalArgumentException("ServiceManager:the params is null");
            }
        }
    }


    public static Map<Object, ServiceCreator> getServiceClassMap() {
        return SERVICE_CLASS_MAP;
    }

    private static String transformClassKey(Class c) {
        return c.getName();
    }

    public static final <T> void add(Object objectKey, String serviceImplClassName, boolean isSingleton) {
        add(objectKey, new DefaultConstructServiceCreator<T>(serviceImplClassName, isSingleton));
    }

    public static final <T> void add(Class<T> serviceKey, Class<T> serviceImpl, boolean isSingleton) {
        add(transformClassKey(serviceKey), new DefaultConstructServiceCreator<T>(serviceImpl, isSingleton));
    }

    public static final <T> void add(Object serviceKey, T serviceImpl) {
        add(serviceKey, new InstantServiceCreator<T>(serviceImpl));
    }

    public static final <T> void add(Class<T> service, ServiceCreator<T> creator) {
        add(transformClassKey(service), creator);
    }

    public static final <T> void add(Object serviceKey, ServiceCreator<T> creator) {
        checkNotNull(serviceKey, creator);
        SERVICE_CLASS_MAP.put(serviceKey, creator);
    }

    public static final <T> T get(Object serviceKey, Object... args) {
        return load(serviceKey, args);
    }

    public static final <T> T get(Class<T> serviceClass, Object... args) {
        return load(transformClassKey(serviceClass), args);
    }

    private static <T> T load(Object serviceKey, Object... constructAgs) {
        checkNotNull(serviceKey, constructAgs);
        ServiceCreator serviceCreator = SERVICE_CLASS_MAP.get(serviceKey);
        if (serviceCreator == null) {
            throw new IllegalArgumentException("the service" + serviceKey + " doesn't register." +
                    "you can call ServiceManager add() before use a service");
        }

        return (T) serviceCreator.create(constructAgs);
    }

}
