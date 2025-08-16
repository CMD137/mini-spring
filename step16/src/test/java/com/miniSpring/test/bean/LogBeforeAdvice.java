package com.miniSpring.test.bean;

import com.miniSpring.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class LogBeforeAdvice implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("前置通知: 方法 " + method.getName() + " 即将执行");
    }
}
