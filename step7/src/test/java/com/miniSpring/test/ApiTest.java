package com.miniSpring.test;

import com.miniSpring.beans.factory.support.DefaultListableBeanFactory;
import com.miniSpring.beans.factory.xml.XmlBeanDefinitionReader;
import com.miniSpring.context.support.ClassPathXmlApplicationContext;
import com.miniSpring.test.bean.UserService;
import com.miniSpring.test.common.MyBeanFactoryPostProcessor;
import com.miniSpring.test.common.MyBeanPostProcessor;
import org.junit.jupiter.api.Test;


public class ApiTest {
    @Test
    public void test_xml() {
        // 1.初始化 BeanFactory
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        applicationContext.registerShutdownHook();

        // 2. 获取Bean对象调用方法
        UserService userService = applicationContext.getBean("userService", UserService.class);
        String result = userService.queryUserInfo();
        System.out.println("测试结果：" + result);
    }

}
