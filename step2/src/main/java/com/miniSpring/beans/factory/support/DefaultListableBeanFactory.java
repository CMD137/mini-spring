package com.miniSpring.beans.factory.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.config.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends  AbstractAutowireCapableBeanFactory implements  BeanDefinitionRegistry{

    //bean注册表
    private Map<String, BeanDefinition> beanDefinitionMap =new ConcurrentHashMap<>();


    @Override
    protected BeanDefinition getBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null){
            throw new BeansException("No bean named '" + beanName + "' is defined");
        }
        return beanDefinition;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinitionMap.containsKey(beanName)){
            throw new BeansException(beanName + "already exists");
        }
        beanDefinitionMap.put(beanName,beanDefinition);
    }
}
