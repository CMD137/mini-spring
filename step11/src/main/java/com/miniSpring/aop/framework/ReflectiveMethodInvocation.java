package com.miniSpring.aop.framework;

import org.aopalliance.intercept.MethodInvocation;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

// 方法调用的具体实现，负责执行拦截器链和目标方法
public class ReflectiveMethodInvocation implements MethodInvocation {
    protected final Object target; // 目标对象
    protected final Method method; // 目标方法
    protected final Object[] arguments; // 方法参数
    protected final List<MethodInterceptor> methodInterceptorList; // 拦截器链
    protected int currentInterceptorIndex = -1; // 当前执行到的拦截器索引

    public ReflectiveMethodInvocation(Object target, Method method, Object[] arguments,
                                      List<MethodInterceptor> interceptors) {
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.methodInterceptorList = interceptors;
    }

    @Override
    public Object proceed() throws Throwable {
        // 如果所有拦截器都执行完了，则调用目标方法
        if (currentInterceptorIndex == methodInterceptorList.size() - 1) {
            return method.invoke(target, arguments);
        }

        // 执行下一个拦截器
        currentInterceptorIndex++;
        MethodInterceptor interceptor = methodInterceptorList.get(currentInterceptorIndex);
        return interceptor.invoke(this); // 将当前调用对象传递给拦截器
    }

    public Object getTarget() {
        return target;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public AccessibleObject getStaticPart() {
        return method;
    }
}

