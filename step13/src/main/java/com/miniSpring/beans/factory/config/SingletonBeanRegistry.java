package com.miniSpring.beans.factory.config;

public interface SingletonBeanRegistry {
    /**
     * 根据 Bean 名称获取单例对象。
     */
    Object getSingleton(String beanName);

    /**
     * 注册单例对象到容器，名称唯一。
     */
    void registerSingleton(String beanName, Object singletonObject);
}
