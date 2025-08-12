package com.miniSpring.aop;

import org.aopalliance.intercept.MethodInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * AdvisedSupport 是 AOP 代理配置的核心承载类，
 * 用于保存目标对象、方法拦截器链和方法匹配器等信息。
 *
 * 主要职责：
 *  - 保存被代理的目标对象（TargetSource）
 *  - 保存方法拦截器链（List<MethodInterceptor>）
 *  - 保存方法匹配器（MethodMatcher），用于判断是否应用拦截器
 *
 * 这样设计方便代理生成时统一读取相关配置，
 * 并支持多拦截器的链式调用，增强灵活性。
 */
public class AdvisedSupport {

    /**
     * 封装目标对象及其类型
     */
    private TargetSource targetSource;

    /**
     * 方法拦截器链，可以包含多个拦截器
     */
    private List<MethodInterceptor> methodInterceptorList = new ArrayList<>();

    /**
     * 方法匹配器，判断某个方法是否需要被增强
     */
    private MethodMatcher methodMatcher;

    // ========== getter/setter ==========

    public TargetSource getTargetSource() {
        return targetSource;
    }



    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public List<MethodInterceptor> getMethodInterceptorList() {
        return methodInterceptorList;
    }

    public void setMethodInterceptorList(List<MethodInterceptor> methodInterceptorList) {
        this.methodInterceptorList = methodInterceptorList;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

    // ========== 方便添加单个拦截器的辅助方法 ==========

    /**
     * 方便向拦截器链添加一个拦截器
     */
    public void addMethodInterceptor(MethodInterceptor interceptor) {
        this.methodInterceptorList.add(interceptor);
    }
}

