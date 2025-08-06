package com.miniSpring.context;

import com.miniSpring.beans.BeansException;

/**
 *  提供了对 Spring 容器的配置和刷新能力，是启动和重置容器的关键接口。
 *  它让 ApplicationContext 不仅是“只读”的 Bean 工厂，更是可控制生命周期的完整容器。
 */
public interface ConfigurableApplicationContext extends ApplicationContext {

    /**
     * 刷新（启动）容器
     *
     * @throws BeansException
     */
    void refresh() throws BeansException;

}

