package com.dc.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CalculatorProperties
 *
 * @title CalculatorProperties
 * @Description
 * @Author donglongcheng01
 * @Date 2019-07-22
 **/
@Data
@ConfigurationProperties(prefix = "dc.calculator")
public class CalculatorProperties {

    private String calculatorName;

}
