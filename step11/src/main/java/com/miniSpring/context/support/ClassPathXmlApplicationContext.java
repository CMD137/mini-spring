package com.miniSpring.context.support;

import com.miniSpring.beans.BeansException;

/**
 * 应用上下文实现类，从类路径下的 XML 文件中加载 Bean 定义。
 * 继承自 AbstractXmlApplicationContext，具体实现了配置路径的获取方式。
 *
 * 一旦构造方法接收到配置路径，会立即调用 refresh() 方法，完成整个 IoC 容器的创建与初始化流程。
 */
public class ClassPathXmlApplicationContext extends AbstractXmlApplicationContext {

    // 配置文件路径列表（如 "classpath:spring.xml"）
    private String[] configLocations;

    public ClassPathXmlApplicationContext() {
    }

    /**
     * 构造函数：接收单个配置路径，转换为数组形式
     * 并立即触发上下文刷新流程（加载 BeanDefinition + 初始化容器）
     *
     * @param configLocations XML 配置文件路径（单个）
     * @throws BeansException 异常处理
     */
    public ClassPathXmlApplicationContext(String configLocations) throws BeansException {
        this(new String[]{configLocations});
    }

    /**
     * 构造函数：接收多个配置路径
     * 并立即触发上下文刷新流程（加载 BeanDefinition + 初始化容器）
     *
     * @param configLocations XML 配置文件路径数组
     * @throws BeansException 异常处理
     */
    public ClassPathXmlApplicationContext(String[] configLocations) throws BeansException {
        // 设置配置路径
        this.configLocations = configLocations;

        // 刷新容器：完成 BeanFactory 创建、加载配置、注册 Bean 等一系列动作
        refresh();
    }

    /**
     * 提供配置文件路径给抽象父类用于加载 BeanDefinition
     *
     * @return 配置路径数组
     */
    @Override
    protected String[] getConfigLocations() {
        return configLocations;
    }
}
