package com.manaplastic.backend.constant.customAnotation;

import com.manaplastic.backend.constant.LogType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogActivity {
    String action();
    String description() default "";
    LogType logType() default LogType.INFO;
}