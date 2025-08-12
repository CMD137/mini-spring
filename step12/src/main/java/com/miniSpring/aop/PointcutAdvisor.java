package com.miniSpring.aop;

/**
 * 带有切点（Pointcut）定义的 Advisor 接口。
 *
 * 该接口不仅封装增强逻辑（Advice），
 * 还包含用于匹配连接点的切点规则（Pointcut），
 * 用于确定在哪些方法上应用对应的增强。
 *
 * 一般用于基于切点表达式的通知配置，
 * 是 Spring AOP 中最常用的 Advisor 类型。
 */
public interface PointcutAdvisor extends Advisor {

    /**
     * 返回此 Advisor 关联的切点（Pointcut），
     * 用于匹配需要应用 Advice 的连接点。
     */
    Pointcut getPointcut();

}

