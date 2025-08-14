package com.miniSpring.aop;

import java.lang.reflect.Method;

public interface MethodMatcher {
    /**
     * 判断给定方法在指定目标类中是否匹配切点规则。
     */
    boolean matches(Method method, Class<?> targetClass);
}
