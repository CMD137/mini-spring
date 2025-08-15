package com.miniSpring.aop.adapter;

import com.miniSpring.aop.MethodAroundAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * MethodAroundAdviceInterceptor 是环绕通知到方法拦截器的适配器。
 *
 * 它将具体的 MethodAroundAdvice 转换成 MethodInterceptor 接口，
 * 使环绕通知可以参与统一的拦截器链调用流程。
 */
public class MethodAroundAdviceInterceptor implements MethodInterceptor {

    private MethodAroundAdvice advice;

    // 添加无参构造函数,用于实例化（关键）
    public MethodAroundAdviceInterceptor() {
    }

    public MethodAroundAdviceInterceptor(MethodAroundAdvice advice) {
        this.advice = advice;
    }

    /**
     * 拦截方法调用，将环绕通知委托给 MethodAroundAdvice
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return advice.around(
                methodInvocation.getMethod(),
                methodInvocation.getArguments(),
                methodInvocation.getThis(),
                methodInvocation
        );
    }
}
