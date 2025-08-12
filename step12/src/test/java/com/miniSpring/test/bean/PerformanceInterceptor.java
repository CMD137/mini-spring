package com.miniSpring.test.bean;

import org.aopalliance.intercept.MethodInvocation;
import org.aopalliance.intercept.MethodInterceptor;

public class PerformanceInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.print("监控拦截器 - Before");
        System.out.println("\t方法名称：" + invocation.getMethod());
        long start = System.currentTimeMillis();

        Object result = invocation.proceed();

        System.out.print("监控拦截器 - After");
        System.out.println("\t方法耗时：" + (System.currentTimeMillis() - start) + "ms");
        return result;
    }
}
