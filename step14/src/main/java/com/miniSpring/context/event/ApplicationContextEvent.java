package com.miniSpring.context.event;

import com.miniSpring.context.ApplicationContext;
import com.miniSpring.context.ApplicationEvent;

/**
 * 应用上下文事件基类。
 * 继承自 {@link ApplicationEvent}，用于封装与 {@link ApplicationContext} 相关的事件。
 * 所有容器级别的事件（如刷新、启动、关闭）都应继承该类。
 */
public class ApplicationContextEvent extends ApplicationEvent {

    /**
     * 创建应用上下文事件。
     */
    public ApplicationContextEvent(Object source) {
        super(source);
    }

    /**
     * 获取触发该事件的 {@link ApplicationContext}。
     */
    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext) getSource();
    }

}

