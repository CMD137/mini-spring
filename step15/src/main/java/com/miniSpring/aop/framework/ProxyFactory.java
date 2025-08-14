package com.miniSpring.aop.framework;

import com.miniSpring.aop.AdvisedSupport;

/**
 * ProxyFactory 是代理对象的工厂类，
 * 负责根据配置信息创建对应类型的 AOP 代理。
 *
 * 核心职责：
 *  - 持有切面配置（AdvisedSupport）对象，
 *    包含目标对象、方法拦截器链、方法匹配器等信息。
 *  - 根据 advisedSupport 中的配置决定使用哪种代理方式：
 *      * 如果设置为代理目标类（isProxyTargetClass() == true），
 *        则使用基于 CGLIB 的代理（Cglib2AopProxy），
 *      * 否则使用基于 JDK 动态代理（JdkDynamicAopProxy）。
 *  - 提供统一的 getProxy() 方法，返回代理后的对象。
 */
public class ProxyFactory {

    private AdvisedSupport advisedSupport;

    /**
     * 构造函数，注入切面配置信息
     * @param advisedSupport 包含目标对象和代理配置
     */
    public ProxyFactory(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    /**
     * 创建代理对象的入口方法，
     * 根据配置选择代理方式并返回代理实例
     * @return 代理对象（目标对象的代理）
     */
    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    /**
     * 根据 advisedSupport 的配置决定代理实现类：
     *  - 代理目标类时返回 Cglib2AopProxy 实例
     *  - 否则返回 JdkDynamicAopProxy 实例
     */
    private AopProxy createAopProxy() {
        if (advisedSupport.isProxyTargetClass()) {
            return new Cglib2AopProxy(advisedSupport);
        }
        return new JdkDynamicAopProxy(advisedSupport);
    }

}

