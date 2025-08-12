package com.miniSpring.test.bean;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class OrderInterceptor implements MethodInterceptor {

    private final String name;

    public OrderInterceptor(String name) {
        this.name = name;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println(name + " - Before");
        Object result = invocation.proceed();
        System.out.println(name + " - After");
        return result;
    }
}
