package com.miniSpring.test.bean;

import com.miniSpring.aop.MethodBeforeAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class OrderInterceptor implements MethodBeforeAdvice {


    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.print("OrderInterceptor拦截器 - Before：");
        System.out.println("\t拦截方法名称：" + method.getName());
    }
}
