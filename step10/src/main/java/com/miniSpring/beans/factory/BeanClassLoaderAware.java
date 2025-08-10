package com.miniSpring.beans.factory;

/**
 * 使 Bean 能感知用于加载 Bean 类的 ClassLoader。
 * 实现该接口的 Bean 在初始化时会被注入 ClassLoader。
 */
public interface BeanClassLoaderAware extends Aware {

    void setBeanClassLoader(ClassLoader classLoader);

}

