package com.miniSpring.beans.factory;

import com.miniSpring.beans.BeansException;

/**
 * 使 Bean 能感知所属的 BeanFactory 容器。
 * 实现该接口的 Bean，在初始化时会被注入当前 BeanFactory。
 */
public interface BeanFactoryAware extends Aware {
    void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}

