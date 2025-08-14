package com.miniSpring.aop.framework;

/**
 * AOP 代理接口，定义获取代理对象的方法。
 *
 * 实现该接口的类负责生成目标对象的代理实例，
 * 以支持面向切面编程（AOP）的功能，如方法拦截、增强等。
 */
public interface AopProxy {

    /**
     * 返回目标对象的代理对象。
     * 代理对象会包装目标对象，并在方法调用时织入增强逻辑。
     */
    Object getProxy();
}
