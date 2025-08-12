package com.miniSpring.context;

import com.miniSpring.beans.BeansException;

/**
 *  提供了容器生命周期管理和上下文配置的扩展功能
 */
public interface ConfigurableApplicationContext extends ApplicationContext {

    //刷新（启动）容器
    void refresh() throws BeansException;

    // 注册一个关闭钩子
    void registerShutdownHook();

    // 关闭容器
    void close();
}

