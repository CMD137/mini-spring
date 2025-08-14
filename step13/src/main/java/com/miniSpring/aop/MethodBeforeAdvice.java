package com.miniSpring.aop;

import java.lang.reflect.Method;

/**
 * 方法执行前的回调通知。
 * 在目标方法调用前执行，可用于检查参数、权限等。
 */
public interface MethodBeforeAdvice extends BeforeAdvice {

    void before(Method method, Object[] args, Object target) throws Throwable;

}

