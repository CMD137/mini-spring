package com.miniSpring.aop;

import org.aopalliance.aop.Advice;

/**
 * 环绕通知标记接口。
 * 环绕通知可以在目标方法执行前后都进行增强操作，
 * 并且可以控制是否执行目标方法。
 */
public interface AroundAdvice extends Advice {
}
