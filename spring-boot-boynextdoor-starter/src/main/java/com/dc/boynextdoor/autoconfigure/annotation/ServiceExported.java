package com.dc.boynextdoor.autoconfigure.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Documented   表示这个注解要加入到doc文档中
 * target   表示这个注解是标记在class上的
 * retention    表示这个注解的解析发生在运行时
 * Inherited    表示如果某个类parent加了这个注解，则其子类child自动就有了这个注解，解析的时候回自动往上找
 * Component    表示这是一个Spring的bean，需要注入到容器的
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
public @interface ServiceExported {
}
