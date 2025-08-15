package com.miniSpring.aop.adapter;

import com.miniSpring.aop.MethodAfterAdvice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * MethodAfterAdviceInterceptor 是后置通知（After Advice）到方法拦截器（MethodInterceptor）的适配器。
 *
 * 它将具体的 MethodAfterAdvice 转换成 MethodInterceptor 接口，
 * 使得后置通知能够参与统一的拦截器链调用流程。
 *
 * 作用：
 * - 解耦通知接口和拦截器接口，方便将不同类型的通知统一管理和执行。
 * - 让后置通知可以像环绕通知一样参与方法调用的拦截器链，实现链式调用。
 */
public class MethodAfterAdviceInterceptor implements MethodInterceptor {

    /**
     * 持有具体的后置通知实现
     */
    private MethodAfterAdvice advice;

    // 添加无参构造函数,用于实例化（关键）
    public MethodAfterAdviceInterceptor() {
    }

    public MethodAfterAdviceInterceptor(MethodAfterAdvice advice) {
        this.advice = advice;
    }

    /**
     * 拦截方法调用，先执行目标方法，然后执行后置通知
     *
     * @param methodInvocation 当前方法调用的封装，包含目标对象、方法、参数和拦截器链
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        // 执行调用链，最终执行目标方法
        Object returnValue = methodInvocation.proceed();
        // 调用后置通知
        advice.after(methodInvocation.getMethod(), methodInvocation.getArguments(),
                methodInvocation.getThis(), returnValue);
        return returnValue;
    }
}
