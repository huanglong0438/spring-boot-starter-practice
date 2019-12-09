package com.dc.boynextdoor.autoconfigure.reference;

import com.dc.boynextdoor.autoconfigure.AbstractRegister;
import com.dc.boynextdoor.autoconfigure.annotation.ServiceReference;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义的登记bean的类，会扫描类引用的{@code @VanReference}属性，确定需要生成代理的类，
 * 然后生成一个FactoryBean代理工厂注入到registry
 * <p>整个是一套自定义的注解扫描工具，扫描并按自定义的方式加载类</p>
 *
 * @title ServiceReferenceRegistrar
 * @Description 自定义的登记bean的类，会扫描类引用的{@code @VanReference}属性，确定需要生成代理的类并生成
 * @Author donglongcheng01
 * @Date 2019-10-18
 **/
public class ServiceReferenceRegistrar extends AbstractRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanNameGenerator beanNameGenerator = new ServiceReferenceBeanNameGenerator();
        Collection<BeanDefinition> candidates = getCandidates(resourceLoader);

        // 从xxxLocalServiceImpl中找到注解了@ServiceReference的属性，然后获取它们的Class
        Set<Class> references = candidates.stream()
                .flatMap(candidate -> {
                    Class<?> clazz = getClass(candidate.getBeanClassName());

                    return FieldUtils.getAllFieldsList(clazz)
                            .stream()
                            .filter(f -> f.getAnnotation(ServiceReference.class) != null)
                            .map(Field::getType);
                }).collect(Collectors.toSet());

        // 给@ServiceReference的Class设置为ServiceReferenceFactoryBean（FactoryBean）的bd，
        // 然后设置注册中心bndConfigService
        // 最后注册到registry里
        references.forEach(fieldType -> {
            BeanDefinition bd = BeanDefinitionBuilder.rootBeanDefinition(ServiceReferenceFactoryBean.class)
                    .addPropertyReference("bndConfigService", "bndConfigService")
                    .addConstructorArgValue(fieldType)
                    .getBeanDefinition();
            String name = beanNameGenerator.generateBeanName(bd, registry);
            bd.setAttribute("factoryBeanObjectType", fieldType.getName());
            registry.registerBeanDefinition(name, bd);
        });

    }

    /**
     * 自定义了一个scanner，然后定义了扫描的规则(filter)，然后设置了资源加载方式resourceLoader，
     * 最后执行scanner的方法扫描了basePackage包，过滤出来了带有@ServiceReference的beanDefinition
     *
     * @param resourceLoader 资源加载器
     * @return eg. xxxLocalServiceImpl，这个本地的bean中使用了@ServiceReference注解引用了别人
     */
    private Collection<BeanDefinition> getCandidates(ResourceLoader resourceLoader) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false, environment);
        scanner.addIncludeFilter(new AbstractTypeHierarchyTraversingFilter(true, false) {
            @Override
            protected boolean matchClassName(String className) {
                try {
                    Class<?> clazz = Class.forName(className);
                    List<Field> fields = FieldUtils.getAllFieldsList(clazz);
                    return fields
                            .stream()
                            .anyMatch(f -> f.getAnnotationsByType(ServiceReference.class) != null);
                } catch (ClassNotFoundException e) {
                    throw new BeanInitializationException("class not found when matching class name", e);
                }
            }
        });
        scanner.setResourceLoader(resourceLoader);
        return getBasePackages()
                .stream().flatMap(basePackage -> scanner.findCandidateComponents(basePackage).stream())
                .collect(Collectors.toSet());
    }
}
