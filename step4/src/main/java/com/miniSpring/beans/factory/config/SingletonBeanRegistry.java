package com.miniSpring.beans.factory.config;

public interface SingletonBeanRegistry {
    Object getSingleton(String beanName);
}
