package com.miniSpring.beans.factory.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.config.BeanDefinition;

import java.lang.reflect.Constructor;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {
    //定义了一个创建对象的实例化策略属性类 InstantiationStrategy instantiationStrategy，这里我们选择了 Cglib 的实现类
    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;
        try {
            bean = createBeanInstance(beanDefinition, beanName, args);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }

        addSingleton(beanName, bean);
        return bean;
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) {
        Constructor constructorToUse = null;
        Class beanClass = beanDefinition.getBeanClass();
        //通过 beanClass.getDeclaredConstructors() 方式可以获取到你所有的构造函数，是一个集合
        Constructor[] declaredConstructors = beanClass.getDeclaredConstructors();

        //循环比对匹配构造函数与args，此处简化为数量对比，实际还需比对入参类型
        for (Constructor ctor : declaredConstructors) {
            if (null != args && ctor.getParameterTypes().length == args.length) {
                constructorToUse = ctor;
                break;
            }
        }

        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructorToUse, args);
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

}