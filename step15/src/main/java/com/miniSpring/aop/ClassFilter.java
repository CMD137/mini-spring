package com.miniSpring.aop;

public interface ClassFilter {
    /**
     * 判断给定类是否匹配切点规则。
     */
    boolean matches(Class<?> clazz);
}
