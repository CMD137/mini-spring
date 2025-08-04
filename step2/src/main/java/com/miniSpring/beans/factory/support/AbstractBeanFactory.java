package com.miniSpring.beans.factory.support;

import com.miniSpring.beans.factory.BeanFactory;
import com.miniSpring.beans.factory.config.BeanDefinition;


public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {
    @Override
    public Object getBean(String beanName) {
        Object bean = getSingleton(beanName);
        //如果bean实例已经存在，直接返回；
        if (bean != null) {
            return bean;
        }

        //不存在，才创建并返回：
        BeanDefinition beanDefinition = getBeanDefinition(beanName);
        return createBean(beanName,beanDefinition);
    }

    protected abstract BeanDefinition getBeanDefinition(String beanName);
    protected abstract Object createBean(String beanName,BeanDefinition beanDefinition);
}
