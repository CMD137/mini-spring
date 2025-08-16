package com.miniSpring.beans.factory;

import com.miniSpring.beans.BeansException;

public interface BeanFactory {
    Object getBean(String beanName) throws BeansException;
    Object getBean(String name, Object... args) throws BeansException;
    <T> T getBean(String name, Class<T> requiredType) throws BeansException;

    <T> T getBean(Class<T> requiredType) throws BeansException;
}
