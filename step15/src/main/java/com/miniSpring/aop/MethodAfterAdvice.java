package com.miniSpring.aop;

import java.lang.reflect.Method;

/**
 * 方法执行后的回调通知。
 * 在目标方法调用后执行，可用于日志记录、清理资源等操作。
 */
public interface MethodAfterAdvice extends AfterAdvice {

    /**
     * 目标方法执行后调用
     */
    void after(Method method, Object[] args, Object target, Object returnValue) throws Throwable;
}
