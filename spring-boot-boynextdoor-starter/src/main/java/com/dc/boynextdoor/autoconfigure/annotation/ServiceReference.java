package com.dc.boynextdoor.autoconfigure.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * Documented 加入到文档中
 * Target 该属性标注到<b>字段</b>上，表示对该属性进行动态代理
 * Retention 运行时解析注解
 * Inherited 加了该注解的类，其子类也会继承该注解
 * Autowired 自动依赖注入
 * </pre>
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Autowired
public @interface ServiceReference {
}
