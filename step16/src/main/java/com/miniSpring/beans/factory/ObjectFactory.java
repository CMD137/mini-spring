package com.miniSpring.beans.factory;

import com.miniSpring.beans.BeansException;

/**
 * 对象工厂接口，用于返回指定类型的对象实例。
 * - 可能返回共享的单例对象，也可能返回新的独立对象，取决于具体实现。
 * - 在 Spring 三级缓存中，常用于提前暴露 Bean 的引用，以解决循环依赖问题。
 */
public interface ObjectFactory<T> {

    /**
     * 获取对象实例
     *
     * @return 返回指定类型的对象
     * @throws BeansException 获取过程中可能抛出的异常
     */
    T getObject() throws BeansException;

}
