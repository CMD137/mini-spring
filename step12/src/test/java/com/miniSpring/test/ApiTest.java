package com.miniSpring.test;

import com.miniSpring.aop.AdvisedSupport;
import com.miniSpring.aop.TargetSource;
import com.miniSpring.aop.aspectj.AspectJExpressionPointcut;
import com.miniSpring.aop.framework.Cglib2AopProxy;
import com.miniSpring.aop.framework.JdkDynamicAopProxy;
import com.miniSpring.test.bean.IUserService;
import com.miniSpring.test.bean.OrderInterceptor;
import com.miniSpring.test.bean.PerformanceInterceptor;
import com.miniSpring.test.bean.UserService;
import org.junit.jupiter.api.Test;



public class ApiTest {
    @Test
    public void testSingleInterceptor() {
        // 目标对象
        IUserService target = new UserService();

        // 组装代理信息
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTargetSource(new TargetSource(target));
        advisedSupport.setMethodMatcher(new AspectJExpressionPointcut("execution(* com.miniSpring.test.bean.IUserService.*(..))"));

        //添加拦截器
        advisedSupport.addMethodInterceptor(new PerformanceInterceptor());

        // 代理对象(JdkDynamicAopProxy)
        IUserService proxy_jdk = (IUserService) new JdkDynamicAopProxy(advisedSupport).getProxy();
        // 测试调用
        System.out.println("\n测试结果：" + proxy_jdk.queryUserName());

        // 代理对象(Cglib2AopProxy)
        IUserService proxy_cglib = (IUserService) new Cglib2AopProxy(advisedSupport).getProxy();
        // 测试调用
        System.out.println("\n测试结果：" + proxy_cglib.queryUserName());
    }

    @Test
    public void testMultiInterceptor() {
        // 目标对象
        IUserService target = new UserService();

        // 组装代理信息
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTargetSource(new TargetSource(target));
        advisedSupport.setMethodMatcher(new AspectJExpressionPointcut("execution(* com.miniSpring.test.bean.IUserService.*(..))"));

        //添加拦截器
        advisedSupport.addMethodInterceptor(new OrderInterceptor("拦截器A"));
        advisedSupport.addMethodInterceptor(new PerformanceInterceptor());
        advisedSupport.addMethodInterceptor(new OrderInterceptor("拦截器B"));

        // 代理对象(JdkDynamicAopProxy)
        IUserService proxy_jdk = (IUserService) new JdkDynamicAopProxy(advisedSupport).getProxy();
        // 测试调用
        System.out.println("\n测试结果：" + proxy_jdk.queryUserName());

        // 代理对象(Cglib2AopProxy)
        IUserService proxy_cglib = (IUserService) new Cglib2AopProxy(advisedSupport).getProxy();
        // 测试调用
        System.out.println("\n测试结果：" + proxy_cglib.queryUserName());
    }


}
