# spring-boot-starter-practice
spring的自定义starter demo

1.新建Maven项目，在项目的POM文件中定义使用的依赖；  
2.新建配置类，写好配置项和默认的配置值，指明配置项前缀；  
3.新建自动装配类，使用@Configuration和@Bean来进行自动装配；  
4.新建`spring.factories`文件，指定Starter的自动装配类；  

## Calculator
简单的starter的测试工程，模拟其它starter的实现，自动生成并注入计算器对象

## boy next door（建设中）
一个基于SpringBoot的starter开发的RPC框架，  
序列化计划采用Google的ProtoBuffer，  
服务采用**基于NIO多路复用技术的**netty框架，支持高并发情况下快速响应

## fake-lombok（暂停）

打算搞一个自定义的编译时织入get和set的注解，资料不全，暂时hold

## TODO项目