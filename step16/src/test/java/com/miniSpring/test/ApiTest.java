package com.miniSpring.test;

import com.miniSpring.context.support.ClassPathXmlApplicationContext;
import com.miniSpring.test.bean.IUserService;
import com.miniSpring.test.bean.TempService;
import org.junit.jupiter.api.Test;



public class ApiTest {
    @Test
    public void test() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        TempService tempService = (TempService) applicationContext.getBean("tempService");

        tempService.queryUserInfo();
        userService.useTempService();
    }

}
