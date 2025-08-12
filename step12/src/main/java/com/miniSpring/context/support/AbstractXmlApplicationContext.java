package com.miniSpring.context.support;

import com.miniSpring.beans.factory.support.DefaultListableBeanFactory;
import com.miniSpring.beans.factory.xml.XmlBeanDefinitionReader;

/**
 * 基于 XML 配置文件的抽象应用上下文，实现了从配置路径中加载 Bean 定义的功能。
 * 继承自 AbstractRefreshableApplicationContext，具备“可刷新 BeanFactory”的能力。
 *
 * 该类的主要职责是调用 XmlBeanDefinitionReader 从 XML 文件中读取 BeanDefinition。
 * 子类需要提供配置文件的路径（如 ClassPathXmlApplicationContext）。
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableApplicationContext {

    /**
     * 加载 Bean 定义：使用 XmlBeanDefinitionReader 解析 XML 配置文件，
     * 并将解析结果注册到传入的 BeanFactory 中。
     *
     * @param beanFactory 用于注册解析后的 Bean 定义
     */
    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
        // 创建 XML 解析器，传入 BeanFactory 和资源加载器（ApplicationContext 本身）
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory, this);

        // 获取配置文件路径
        String[] configLocations = getConfigLocations();

        // 加载 XML 配置中的 Bean 定义
        if (null != configLocations){
            beanDefinitionReader.loadBeanDefinitions(configLocations);
        }
    }

    /**
     * 获取配置文件路径（如 classpath:spring.xml）
     * 该方法交由子类实现，允许灵活指定不同的配置资源。
     *
     * @return 包含配置文件路径的字符串数组
     */
    protected abstract String[] getConfigLocations();
}


