package com.miniSpring.aop;

/**
 * TargetSource 封装了被代理的目标对象。
 *
 * 作用：
 * - 提供目标对象实例，代理对象调用时最终委托给它。
 * - 提供目标对象的类型信息（通常是接口数组），方便代理类创建代理。
 *
 * 说明：
 * - getTarget() 返回实际被代理的目标对象实例。
 * - getTargetClass() 返回目标对象实现的接口数组，适用于 JDK 动态代理需要接口的情况。
 *
 * 这个类的设计使得代理和目标对象解耦，方便扩展和目标对象的替换。
 */
public class TargetSource {

    /**
     * 目标对象实例
     */
    private final Object target;

    /**
     * 构造方法，传入目标对象
     */
    public TargetSource(Object target) {
        this.target = target;
    }

    /**
     * 获取目标对象实现的接口数组
     * 主要用于 JDK 动态代理创建代理对象时需要接口
     */
    public Class<?>[] getTargetClass(){
        return this.target.getClass().getInterfaces();
    }

    /**
     * 获取目标对象实例
     * 代理调用时会委托给这个目标对象执行实际逻辑
     */
    public Object getTarget(){
        return this.target;
    }
}

