package org.nanospark.versionablehelper.core.annotation;

import java.lang.annotation.*;

/**
 * This annotation is used as a flag to describe which property will do crossDayChange
 * when needed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface CrossDayChangeDetect {
}
