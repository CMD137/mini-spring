package com.miniSpring.aop;

import com.miniSpring.util.ClassUtils;

/**
 * 封装被代理的目标对象，提供目标实例和类型信息
 */
public class TargetSource {

    private final Object target;

    public TargetSource(Object target) {
        this.target = target;
    }

    /**
     * 获取目标对象的实际类型（类）
     * 例如：UserService.class
     */
    public Class<?> getTargetType() {
        return this.target.getClass();
    }

    /**
     * 获取目标对象实现的所有接口
     * 例如：[IUserService.class]
     */
    public Class<?>[] getTargetInterfaces() {
        return getTargetType().getInterfaces();
    }

    /**
     * 获取目标对象实例
     */
    public Object getTarget() {
        return this.target;
    }

    public Class<?> getTargetClass() {
        Class<?> clazz = this.target.getClass();
        clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;
        return clazz;
    }

}