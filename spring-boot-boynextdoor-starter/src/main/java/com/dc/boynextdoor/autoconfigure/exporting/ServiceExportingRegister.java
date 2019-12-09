package com.dc.boynextdoor.autoconfigure.exporting;

import com.dc.boynextdoor.autoconfigure.AbstractRegister;
import com.dc.boynextdoor.autoconfigure.annotation.ServiceExported;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ServiceExportingRegister
 *
 * @title ServiceExportingRegister
 * @Description
 * @Author donglongcheng01
 * @Date 2019-12-02
 **/
public class ServiceExportingRegister extends AbstractRegister implements ImportBeanDefinitionRegistrar {

    private static final String BEAN_NAME_SUFFIX = "ServiceExporterRegisterBean";

    /**
     * <p>从{@link SpringBootApplication}所在目录下扫描所有{@link ServiceExported}注解过的类，
     * <p>从这些类的interface中找到需要注册的XXXService接口
     * <p>构造{@link ServiceExporterRegisterBean}并注入到容器
     */
    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 从resourceLoader中找出@ServiceExport注解过的bean
        Collection<BeanDefinition> candidates = getCandidates(resourceLoader);

        AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

        // service interface --> impl class name
        Map<Class, String> exportedClass = Maps.newHashMap();

        /*
            每个@ServiceExport注解过的bean，挖出它所有的接口，筛掉jdk的、spring的、标记接口，剩下的大概率就是要export上去的接口
            然后通过HashMap来进行去重判断，防止一个XXXService被多个impl实现
            最后返回<XXXService, XXXSerivceImpl>对
         */
        List<ServiceNameAndInterface> exportings = candidates.stream()
                .flatMap(candidate -> {
                    // 根据bean生成beanName
                    String serviceBeanName = beanNameGenerator.generateBeanName(candidate, registry);
                    Class<?> clazz = getClass(candidate.getBeanClassName());

                    List<Class<?>> serviceInterfaces = Arrays.stream(ClassUtils.getAllInterfacesForClass(clazz))
                            .filter(c -> !c.getCanonicalName().startsWith("java")) // 不要java开头的接口（jdk官方接口）
                            .filter(c -> !c.getCanonicalName().startsWith("org.springframework")) // 不要Spring的接口
                            .filter(c -> c.getMethods().length > 0) // 要有方法interface（即不要空interface
                            .collect(Collectors.toList());

                    return serviceInterfaces.stream().map(serviceInterface -> {
                        if (exportedClass.containsKey(serviceInterface)) {
                            // 该service api的接口已经被别的bean实现过了，即同一个XXXService不能有两个实现类撞车
                            throw new IllegalStateException("already exported interface " + serviceInterface
                                    + " with bean class name=" + exportedClass.get(serviceInterface));
                        }
                        exportedClass.put(serviceInterface, serviceBeanName);

                        ServiceNameAndInterface configure = new ServiceNameAndInterface();
                        configure.serviceInterface = serviceInterface;
                        configure.serviceName = serviceBeanName;
                        return configure;
                    });
                }).collect(Collectors.toList());

        /*
           对<XXXService, XXXSerivceImpl>遍历，生成ServiceExporterRegisterBean，然后设置里面的属性，最后注册到registry里
         */
        exportings
                .forEach(configure -> {
                    BeanDefinition beanDefinition =
                            BeanDefinitionBuilder.rootBeanDefinition(ServiceExporterRegisterBean.class)
                                    .addPropertyReference("target", configure.serviceName)
                                    .addPropertyValue("serviceInterface", configure.serviceInterface)
                                    .addPropertyReference("bndConfigService", "bndConfigService")
                                    .getBeanDefinition();
                    String beanName = configure.serviceName + BEAN_NAME_SUFFIX;
                    registry.registerBeanDefinition(beanName, beanDefinition);
                });
    }

    private Collection<BeanDefinition> getCandidates(ResourceLoader resourceLoader) {
        // 不要@Component的结果
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false, environment);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ServiceExported.class)); // 要找@ServiceExported的类
        scanner.setResourceLoader(resourceLoader);
        // 获取@SpringBootApplication所在的类下的包，对每个包执行scanner扫描@ServiceExported注解的类，获取beanDefinition
        return getBasePackages().stream()
                .flatMap(basePackage -> scanner.findCandidateComponents(basePackage).stream())
                .collect(Collectors.toSet());
    }

    private static class ServiceNameAndInterface {
        private String serviceName; // 实现类的名字，即XXXServiceImpl
        private Class<?> serviceInterface; // 接口，即XXXService
    }

}
