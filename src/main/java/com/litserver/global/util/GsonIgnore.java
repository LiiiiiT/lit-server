package com.litserver.global.util;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GsonIgnore {

    /***
     * if authorize failed,
     *
     * @return True for yes and False will continue process
     */
    boolean skipSerialize() default true;

    /***
     * if authorize failed,
     *
     * @return True for yes and False will continue process
     */
    boolean skipDeserialize() default true;

}