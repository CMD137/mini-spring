package com.miniSpring.test;

import com.miniSpring.aop.AdvisedSupport;
import com.miniSpring.aop.TargetSource;
import com.miniSpring.aop.aspectj.AspectJExpressionPointcut;
import com.miniSpring.aop.framework.Cglib2AopProxy;
import com.miniSpring.aop.framework.JdkDynamicAopProxy;
import com.miniSpring.context.support.ClassPathXmlApplicationContext;
import com.miniSpring.test.bean.IUserService;
import com.miniSpring.test.bean.OrderInterceptor;
import com.miniSpring.test.bean.PerformanceInterceptor;
import com.miniSpring.test.bean.UserService;
import org.junit.jupiter.api.Test;



public class ApiTest {
    @Test
    public void testSingleInterceptor() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("测试结果：" + userService.queryUserName());
    }



}
