package com.miniSpring.test.bean;

import com.miniSpring.aop.MethodAroundAdvice;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class LogAroundAdvice implements MethodAroundAdvice {
    @Override
    public Object around(Method method, Object[] args, Object target, MethodInvocation invocation) throws Throwable {
        System.out.println("环绕通知: 方法 " + method.getName() + " 执行前");
        Object returnValue = invocation.proceed(); // 执行目标方法或下一个拦截器
        System.out.println("环绕通知: 方法 " + method.getName() + " 执行后, 返回值: " + returnValue);
        return returnValue;
    }
}

