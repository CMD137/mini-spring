package com.miniSpring.context.event;

import com.miniSpring.context.ApplicationContext;

/**
 * 容器刷新事件。
 * 当 {@link ApplicationContext} 初始化或刷新完成时发布。
 */
public class ContextRefreshedEvent extends ApplicationContextEvent {

    /**
     * 创建容器刷新事件。
     *
     * @param source 事件源，即已刷新完成的 {@link ApplicationContext}。
     */
    public ContextRefreshedEvent(Object source) {
        super(source);
    }

}

