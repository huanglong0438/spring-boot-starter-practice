package com.dc.starter.calculator;

import lombok.extern.slf4j.Slf4j;

/**
 * Calculator
 *
 * @title Calculator
 * @Description
 * @Author donglongcheng01
 * @Date 2019-07-22
 **/
@Slf4j
public class Calculator {
    private String calculatorName;

    public Calculator(String calculatorName) {
        this.calculatorName = calculatorName;
    }

    public int add(int x, int y) {
        log.info("current name is " + this.calculatorName);
        return x + y;
    }

    public int minus(int x, int y) {
        log.info("current name is " + this.calculatorName);
        return x - y;
    }
}
