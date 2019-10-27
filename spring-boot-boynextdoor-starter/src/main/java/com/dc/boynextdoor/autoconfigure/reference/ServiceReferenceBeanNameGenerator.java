package com.dc.boynextdoor.autoconfigure.reference;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * 从bd的定义中捞出interface（XXXService），然后依据它来生成beanName(xXXService)，
 * <p>eg. 在引用UserService的时候，使用注解@ServiceReference，就可以自动依据类名生成beanName，进而生成代理bean
 *
 * @title ServiceReferenceBeanNameGenerator
 * @Description
 * @Author donglongcheng01
 * @Date 2019-10-22
 **/
public class ServiceReferenceBeanNameGenerator extends AnnotationBeanNameGenerator {

    /**
     * 从bd的构造参数中提出接口 -> 构造AnnotatedGenericBeanDefinition -> 根据@注解的value生成beanName
     */
    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        AnnotatedBeanDefinition annotatedBeanDefinition =
                new AnnotatedGenericBeanDefinition(extractInterface(definition));
        return super.generateBeanName(annotatedBeanDefinition, registry);
    }

    /**
     * 从bd的构造参数中提出接口
     */
    private Class<?> extractInterface(BeanDefinition beanDefinition) {
        return (Class<?>) beanDefinition.getConstructorArgumentValues()
                .getArgumentValue(0, Class.class).getValue();
    }

}
