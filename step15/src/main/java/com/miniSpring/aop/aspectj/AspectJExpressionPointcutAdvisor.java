package com.miniSpring.aop.aspectj;

import com.miniSpring.aop.Pointcut;
import com.miniSpring.aop.PointcutAdvisor;
import com.miniSpring.core.Ordered;
import org.aopalliance.aop.Advice;

/**
 * 基于 AspectJ 表达式的切点通知器（PointcutAdvisor）实现。
 *
 * 该类封装了：
 * - AspectJ 表达式切点（AspectJExpressionPointcut），用于匹配目标方法
 * - 具体的增强逻辑（Advice）
 * - 以及对应的切点表达式字符串（expression）
 *
 * 通过设置表达式和 Advice，Spring AOP 可以在匹配的方法上应用对应的通知。
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor, Ordered {

    // AspectJ 表达式切点，负责匹配目标方法
    private AspectJExpressionPointcut pointcut;

    // 具体的增强逻辑（通知）
    private Advice advice;

    // AspectJ 切点表达式字符串
    private String expression;

    private int order = Integer.MAX_VALUE; // 默认优先级最低

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


    /**
     * 设置 AspectJ 表达式
     * @param expression 切点表达式字符串
     */
    public void setExpression(String expression){
        this.expression = expression;
    }

    /**
     * 获取切点对象，延迟初始化
     * @return 切点
     */
    @Override
    public Pointcut getPointcut() {
        if (null == pointcut) {
            pointcut = new AspectJExpressionPointcut(expression);
        }
        return pointcut;
    }

    /**
     * 获取增强逻辑
     * @return Advice 通知对象
     */
    @Override
    public Advice getAdvice() {
        return advice;
    }

    /**
     * 设置增强逻辑
     * @param advice 通知对象
     */
    public void setAdvice(Advice advice){
        this.advice = advice;
    }

}


