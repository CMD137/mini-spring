package com.miniSpring.test;

import com.miniSpring.context.support.ClassPathXmlApplicationContext;
import com.miniSpring.test.bean.IUserService;
import org.junit.jupiter.api.Test;



public class ApiTest {
    @Test
    public void test() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("测试结果：" + userService.queryUserInfo());
    }

}
