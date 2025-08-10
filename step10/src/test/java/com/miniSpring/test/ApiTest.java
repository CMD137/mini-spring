package com.miniSpring.test;

import com.miniSpring.context.support.ClassPathXmlApplicationContext;
import com.miniSpring.test.bean.UserService;
import com.miniSpring.test.event.CustomEvent;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;


public class ApiTest {
    @Test
    public void test_event() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        applicationContext.publishEvent(new CustomEvent(applicationContext, 1019129009086763L, "成功了！"));

        applicationContext.registerShutdownHook();
    }

}
