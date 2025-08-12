package com.miniSpring.aop.adapter;

import com.miniSpring.aop.MethodBeforeAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * MethodBeforeAdviceInterceptor 是前置通知（Before Advice）到方法拦截器（MethodInterceptor）的适配器。
 *
 * 它将具体的 MethodBeforeAdvice 转换成 MethodInterceptor 接口，
 * 使得前置通知能够参与统一的拦截器链调用流程。
 *
 * 作用：
 * - 解耦通知接口和拦截器接口，方便将不同类型的通知统一管理和执行。
 * - 让前置通知可以像环绕通知一样参与方法调用的拦截器链，实现链式调用。
 */
public class MethodBeforeAdviceInterceptor implements MethodInterceptor {

    /**
     * 持有具体的前置通知实现
     */
    private MethodBeforeAdvice advice;

    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.advice = advice;
    }

    /**
     * 拦截方法调用，先执行前置通知，然后继续执行调用链或目标方法
     *
     * @param methodInvocation 当前方法调用的封装，包含目标对象、方法、参数和拦截器链
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        // 调用前置通知
        advice.before(methodInvocation.getMethod(), methodInvocation.getArguments(), methodInvocation.getThis());
        // 继续执行调用链，直到执行目标方法
        return methodInvocation.proceed();
    }

    // 添加setter方法，支持XML的property注入
    public void setAdvice(MethodBeforeAdvice advice) {
        this.advice = advice;
    }
}

