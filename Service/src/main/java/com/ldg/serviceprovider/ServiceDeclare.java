package com.ldg.serviceprovider;

import androidx.annotation.NonNull;
import androidx.core.app.RemoteInput;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * spi实现接口使用，provider将作为服务实现类的key
 * <p>
 * created by gui 2020/11/13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceDeclare {
    Class provider();

    boolean singleton() default false;
}
