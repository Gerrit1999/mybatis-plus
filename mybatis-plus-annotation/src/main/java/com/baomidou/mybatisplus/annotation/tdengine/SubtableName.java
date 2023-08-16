package com.baomidou.mybatisplus.annotation.tdengine;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 子表相关
 *
 * @author Gerrit
 * @since 2023/8/14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface SubtableName {

    /**
     * 子表名
     */
    String value();
}
