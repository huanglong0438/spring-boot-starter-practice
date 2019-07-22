package com.dc.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * com.dc.starter.CalculatorAutoConfiguration
 *
 * @title com.dc.starter.CalculatorAutoConfiguration
 * @Description
 * @Author donglongcheng01
 * @Date 2019-07-22
 **/
@Slf4j
@Configuration
@EnableConfigurationProperties(CalculatorProperties.class)
public class CalculatorAutoConfiguration {

    @Autowired
    private CalculatorProperties calculatorProperties;

    @Bean
    @ConditionalOnMissingBean
    public Calculator calculator() {
        return new Calculator(calculatorProperties.getCalculatorName());
    }

}
