package com.miniSpring.beans.factory.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.core.io.Resource;
import com.miniSpring.core.io.ResourceLoader;

/**
 * BeanDefinitionReader 是负责读取 Bean 定义的接口，
 * 其核心功能是从各种资源中加载 Bean 的配置信息，
 * 并将 BeanDefinition 注册到 BeanDefinitionRegistry 中。
 */
public interface BeanDefinitionReader {

    /**
     * 获取注册 BeanDefinition 的注册表
     * @return BeanDefinitionRegistry 实例
     */
    BeanDefinitionRegistry getRegistry();

    /**
     * 获取资源加载器，负责定位和加载资源文件
     * @return ResourceLoader 实例
     */
    ResourceLoader getResourceLoader();

    /**
     * 从单个 Resource（资源）中加载 Bean 定义
     * @param resource 资源对象，如 classpath 下的 XML 配置文件
     * @throws BeansException 加载异常
     */
    void loadBeanDefinitions(Resource resource) throws BeansException;

    /**
     * 从多个 Resource（资源）中批量加载 Bean 定义
     * @param resources 资源数组（可变参数），支持传入多个 Resource
     * @throws BeansException 加载异常
     */
    void loadBeanDefinitions(Resource... resources) throws BeansException;

    /**
     * 从资源路径字符串（location）加载 Bean 定义
     * @param location 资源路径，如 "classpath:beans.xml"
     * @throws BeansException 加载异常
     */
    void loadBeanDefinitions(String location) throws BeansException;

}


