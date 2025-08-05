package com.miniSpring.test;

import com.miniSpring.beans.factory.config.BeanDefinition;
import com.miniSpring.beans.factory.support.DefaultListableBeanFactory;
import com.miniSpring.test.bean.UserService;

public class Test {
    @org.junit.jupiter.api.Test
    public void test_BeanFactory() {
        // 1.初始化 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2. 注入bean
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 3.获取bean
        UserService userService = (UserService) beanFactory.getBean("userService", "CMD137");
        userService.queryUserInfo();
    }

}
