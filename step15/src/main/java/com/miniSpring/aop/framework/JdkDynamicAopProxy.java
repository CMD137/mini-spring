package com.miniSpring.aop.framework;

import com.miniSpring.aop.*;
import com.miniSpring.aop.adapter.MethodAfterAdviceInterceptor;
import com.miniSpring.aop.adapter.MethodAroundAdviceInterceptor;
import com.miniSpring.aop.adapter.MethodBeforeAdviceInterceptor;
import com.miniSpring.aop.aspectj.AspectJExpressionPointcutAdvisor;
import com.miniSpring.beans.BeansException;
import com.miniSpring.core.Ordered;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 基于 JDK 动态代理的 AOP 代理实现类。
 * 通过实现 InvocationHandler 接口，实现对目标对象方法的拦截和增强。
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    /**
     * 封装了被代理对象、方法匹配器和方法拦截器等信息的配置类
     */
    private final AdvisedSupport advisedSupport;

    /**
     * 构造函数，传入被代理的配置信息
     * @param advised 代理相关的配置，包括目标对象、方法匹配器、拦截器等
     */
    public JdkDynamicAopProxy(AdvisedSupport advised) {
        this.advisedSupport = advised;
    }

    /**
     * 创建并返回目标对象的 JDK 动态代理对象
     * @return 代理对象，类型为目标对象的接口类型
     */
    @Override
    public Object getProxy() {
        // 使用当前线程上下文类加载器，目标类接口列表，以及当前对象（作为 InvocationHandler）
        // 1. 获取目标对象的Class（如UserService.class）
        Class<?> targetClass = advisedSupport.getTargetSource().getTargetClass();

        //debug:
        //System.out.println("目标类：" + targetClass.getName());

        // 2. 获取目标对象实现的所有接口（关键：必须是接口数组）
        Class<?>[] interfaces = targetClass.getInterfaces();

        // 3. 验证是否有接口（JDK代理必须基于接口）
        if (interfaces.length == 0) {
            throw new BeansException("目标类 " + targetClass.getName() + " 未实现任何接口，无法使用JDK动态代理");
        }

        //debug:
//        for (Class<?> iface : interfaces) {
//            //System.out.println("代理实现的接口：" + iface.getName());
//            // 应输出：com.miniSpring.test.bean.IUserService
//        }


        // 4. 传入接口数组创建代理
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                interfaces, // 正确：传入接口数组（如[IUserService.class]）
                this
        );
    }

    /**
     * 代理对象的方法调用处理逻辑，实现了方法拦截增强功能
     * @param proxy 代理对象本身（一般不直接使用）
     * @param method 被调用的方法
     * @param args 方法参数
     * @return 方法调用结果
     * @throws Throwable 方法执行过程中抛出的异常
     */

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1. 检查是否是Object类的方法（如toString、hashCode等），这些方法通常不需要增强
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(advisedSupport.getTargetSource().getTarget(), args);
        }


        // 2. 获取匹配的Advisor
        List<AspectJExpressionPointcutAdvisor> eligibleAdvisors = new ArrayList<>();
        for (AspectJExpressionPointcutAdvisor advisor : advisedSupport.getAdvisors()) {
            if (advisor.getPointcut().getMethodMatcher()
                    .matches(method, advisedSupport.getTargetSource().getTarget().getClass())) {
                eligibleAdvisors.add(advisor);
            }
        }

        // 3. 对 Advisors 按 Order 排序
        eligibleAdvisors.sort(Comparator.comparingInt(Ordered::getOrder));


        // 4. 转换 Advisors → MethodInterceptors（使用适配器注册中心）
        List<MethodInterceptor> interceptorChain = new ArrayList<>();
        for (Advisor advisor : eligibleAdvisors) {
            interceptorChain.addAll(adaptAdviceToInterceptor(advisor.getAdvice()));
        }

        // 4. 创建方法调用器，封装目标对象、方法、参数和拦截器链
        ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(
                advisedSupport.getTargetSource().getTarget(),
                method,
                args,
                interceptorChain
        );

        // 5. 执行拦截器链（责任链模式），最终调用目标方法
        return invocation.proceed();

    }

    /**
     * 把 Advice 转换成 MethodInterceptor 列表
     * 模拟 Spring 的 AdvisorAdapterRegistry
     */
    private List<MethodInterceptor> adaptAdviceToInterceptor(Advice advice) {
        List<MethodInterceptor> interceptors = new ArrayList<>();

        if (advice instanceof MethodAroundAdvice) {
            interceptors.add(new MethodAroundAdviceInterceptor((MethodAroundAdvice) advice));
        } else if (advice instanceof MethodBeforeAdvice) {
            interceptors.add(new MethodBeforeAdviceInterceptor((MethodBeforeAdvice) advice));
        } else if (advice instanceof MethodAfterAdvice) {
            interceptors.add(new MethodAfterAdviceInterceptor((MethodAfterAdvice) advice));
        } else if (advice instanceof MethodInterceptor) {
            // 仅对已经实现了 MethodInterceptor 的 Advice 支持直接加入
            interceptors.add((MethodInterceptor) advice);
        } else {
            throw new IllegalArgumentException("Unsupported advice type: " + advice.getClass());
        }

        return interceptors;
    }


}

