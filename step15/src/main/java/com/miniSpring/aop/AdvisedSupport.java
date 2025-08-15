package com.miniSpring.aop;

import com.miniSpring.aop.aspectj.AspectJExpressionPointcutAdvisor;

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

    // 改成存 Advisor 列表，而非 MethodInterceptor 列表
    private List<AspectJExpressionPointcutAdvisor> advisors = new ArrayList<AspectJExpressionPointcutAdvisor>();

    /**
     * 方法匹配器，判断某个方法是否需要被增强
     */
    private MethodMatcher methodMatcher;

    /*
    默认false代表用JDK动态代理，true代表用Cglib。
     */
    private boolean proxyTargetClass = false;

    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }


    // ========== getter/setter ==========

    public TargetSource getTargetSource() {
        return targetSource;
    }



    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public List<AspectJExpressionPointcutAdvisor> getAdvisors() {
        return advisors;
    }

    public void setAdvisors(List<AspectJExpressionPointcutAdvisor> advisors) {
        this.advisors = advisors;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }

    // ========== 方便添加单个拦截器的辅助方法 ==========

    /**
     * 方便向拦截器链添加一个advisor
     */
    public void addAdvisor(AspectJExpressionPointcutAdvisor advisor) {
        this.advisors.add(advisor);
    }
}

