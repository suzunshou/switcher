package io.github.suzunshou.switcher.annotation;

import io.github.suzunshou.switcher.handler.DefaultValueChangeHandler;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Switcher {

    String name() default "";

    Class<?> valueChangeHandler() default DefaultValueChangeHandler.class;
}
