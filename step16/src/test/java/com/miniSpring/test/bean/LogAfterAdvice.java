package com.miniSpring.test.bean;

import com.miniSpring.aop.MethodAfterAdvice;

import java.lang.reflect.Method;

public class LogAfterAdvice implements MethodAfterAdvice {
    @Override
    public void after(Method method, Object[] args, Object target, Object returnValue) throws Throwable {
        System.out.println("后置通知: 方法 " + method.getName() + " 执行完毕, 返回值: " + returnValue);
    }
}

