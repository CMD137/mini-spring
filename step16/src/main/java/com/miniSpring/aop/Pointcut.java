package com.miniSpring.aop;

/**
 * Pointcut（切点）接口
 *
 * 意义：
 * 定义 AOP 中用于匹配目标类与目标方法的规则。
 * 一个 Pointcut 由两部分组成：
 * 1. ClassFilter：类匹配规则，用于筛选哪些类的连接点需要被增强。
 * 2. MethodMatcher：方法匹配规则，用于筛选类中哪些方法需要被增强。
 *
 * 这样 AOP 框架就可以根据 Pointcut 的规则，决定是否对目标方法应用通知（Advice）。
 */
public interface Pointcut {

    /**
     * 返回该切点的类过滤器。
     * @return 类过滤器（不会返回 null）
     */
    ClassFilter getClassFilter();

    /**
     * 返回该切点的方法匹配器。
     * @return 方法匹配器（不会返回 null）
     */
    MethodMatcher getMethodMatcher();
}
