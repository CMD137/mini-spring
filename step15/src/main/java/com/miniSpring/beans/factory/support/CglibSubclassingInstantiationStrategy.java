package com.miniSpring.beans.factory.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.config.BeanDefinition;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.Constructor;

public class CglibSubclassingInstantiationStrategy implements InstantiationStrategy {
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor ctor, Object[] args) throws BeansException {
        //CGLIB 的核心类，用来生成子类对象的工具。
        Enhancer enhancer = new Enhancer();

        //设置“目标类”为被代理的父类（也就是你的 Bean 原始类）,CGLIB 是通过 继承（子类）方式代理的
        enhancer.setSuperclass(beanDefinition.getBeanClass());

        //设置回调对象（Callback），即当代理类的方法被调用时，如何进行“拦截”处理。
        enhancer.setCallback(new NoOp() {
            //NoOp 是 CGLIB 内置的一个回调实现，表示“不做任何增强”。这里只是在演示。可替换为动态代理、懒加载等...
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        });

        /*
        判断是否有构造器：
            如果没有，调用默认构造方法 enhancer.create()
            如果有参数构造方法，则通过 enhancer.create(ctor.getParameterTypes(), args) 创建对象
         */
        if (null == ctor) return enhancer.create();
        return enhancer.create(ctor.getParameterTypes(), args);
    }
}

