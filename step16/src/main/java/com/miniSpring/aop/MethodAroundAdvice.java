package com.miniSpring.aop;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 方法执行前后都可增强的回调通知。
 * 环绕通知可以决定是否执行目标方法，并可在执行前后添加自定义逻辑。
 */
public interface MethodAroundAdvice extends AroundAdvice {

    /**
     * 环绕通知方法
     */
    Object around(Method method, Object[] args, Object target, MethodInvocation invocationMethodChain) throws Throwable;
}

