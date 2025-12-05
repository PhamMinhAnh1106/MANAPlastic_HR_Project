package com.manaplastic.backend.constant.customAnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredPermission {
    String[] value(); // mã quyền

    LogicalOperator logic() default LogicalOperator.OR; // logic 1 hoặc nhiều, mặc định là OR
}