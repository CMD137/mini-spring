package com.miniSpring.test;

import com.miniSpring.context.support.ClassPathXmlApplicationContext;
import com.miniSpring.test.bean.UserService;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;


public class ApiTest {
    @Test
    public void test_prototype() {
        // 1.初始化 BeanFactory
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        applicationContext.registerShutdownHook();

        // 2. 获取Bean对象调用方法
        UserService userService01 = applicationContext.getBean("userService", UserService.class);
        UserService userService02 = applicationContext.getBean("userService", UserService.class);

        // 3. 配置 scope="prototype/singleton"
        System.out.println("userService01:"+userService01);
        System.out.println("userService02:"+userService02);

        // 4.检验userService原型模式（应为false）
        System.out.println("userService01 == userService02?");
        System.out.println(userService01 == userService02);

        // 5. 检验userDao单例模式（应为true）
        System.out.println("userService01.getUserDao() == userService02.getUserDao()?");
        System.out.println(userService01.getUserDao() == userService02.getUserDao());
    }

    @Test
    public void test_factory_bean() {
        // 1.初始化 BeanFactory
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        applicationContext.registerShutdownHook();

        // 2. 调用代理方法
        UserService userService = applicationContext.getBean("userService", UserService.class);
        System.out.println("测试结果：" + userService.queryUserInfo());
    }

}
