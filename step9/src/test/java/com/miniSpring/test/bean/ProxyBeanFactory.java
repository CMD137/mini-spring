package com.miniSpring.test.bean;

import com.miniSpring.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * ProxyBeanFactory 实现了 FactoryBean 接口，用于创建 IUserDao 接口的动态代理实例。
 *
 * 通过动态代理机制，代理 IUserDao 接口的调用，拦截方法调用并返回自定义逻辑结果。
 *
 * 该工厂类演示了如何利用 JDK 动态代理结合 FactoryBean 来生成接口的代理对象，
 * 实现了接口方法的增强和行为定制。
 */
public class ProxyBeanFactory implements FactoryBean<IUserDao> {

    /**
     * 通过 JDK 动态代理创建 IUserDao 接口的代理实例。
     * 代理对象拦截所有方法调用，利用 InvocationHandler 实现方法增强：
     * - 根据方法入参，从模拟的 Map 中查询对应的用户名。
     * - 拼接返回字符串，示意该方法被代理拦截。
     *
     * 关键点说明：
     * - Proxy.newProxyInstance 创建动态代理对象。
     * - 第一个参数为类加载器，通常使用当前线程上下文类加载器。
     * - 第二个参数是代理接口列表，这里代理 IUserDao。
     * - 第三个参数是 InvocationHandler，实现方法调用的拦截逻辑。
     *
     * @return IUserDao 接口的代理对象
     * @throws Exception 创建代理对象时可能抛出的异常
     */
    @Override
    public IUserDao getObject() throws Exception {
        InvocationHandler handler = (proxy, method, args) -> {
            // 模拟数据库或数据源
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("10001", "CMD137");
            hashMap.put("10002", "CMD138");
            hashMap.put("10003", "CMD139");

            // 返回动态代理的结果，说明调用了代理方法
            return "你被代理了 " + method.getName() + "：" + hashMap.get(args[0].toString());
        };

        // 创建并返回代理实例，实现 IUserDao 接口
        return (IUserDao) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{IUserDao.class},
                handler
        );
    }

    /**
     * 返回该 FactoryBean 创建对象的类型，即 IUserDao 接口类型。
     * 容器据此进行类型推断和自动装配。
     *
     * @return IUserDao 的 Class 对象
     */
    @Override
    public Class<?> getObjectType() {
        return IUserDao.class;
    }

    /**
     * 指示该 FactoryBean 创建的对象是否为单例。
     * 返回 true，表示代理对象在容器中是单例，缓存共享。
     *
     * @return true，单例对象
     */
    @Override
    public boolean isSingleton() {
        return true;
    }
}

