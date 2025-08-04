package com.miniSpring.test;

import com.miniSpring.beans.factory.config.BeanDefinition;
import com.miniSpring.beans.factory.support.DefaultListableBeanFactory;
import com.miniSpring.test.bean.UserService;

public class Test {
    @org.junit.jupiter.api.Test
    public void test_BeanFactory(){
        // 1.初始化 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 2.注册 bean
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);
        // 3.第一次获取 bean:新创建的对象。
        UserService userService = (UserService) beanFactory.getBean("userService");
        userService.queryUserInfo();
        // 4.第二次获取 bean from Singleton，从实例map表里取得同一个对象。
        UserService userService_singleton = (UserService) beanFactory.getBean("userService");
        userService_singleton.queryUserInfo();
    }
}
