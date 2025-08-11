package com.miniSpring.context.event;

import com.miniSpring.context.ApplicationContext;

/**
 * 容器关闭事件。
 * 当 {@link ApplicationContext} 被关闭时发布。
 */
public class ContextClosedEvent extends ApplicationContextEvent {

    /**
     * 创建容器关闭事件。
     *
     * @param source 事件源，即被关闭的 {@link ApplicationContext}。
     */
    public ContextClosedEvent(Object source) {
        super(source);
    }

}
