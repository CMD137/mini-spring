package com.miniSpring.context.event;

import com.miniSpring.context.ApplicationEvent;
import com.miniSpring.context.ApplicationListener;

/**
 * 应用事件广播器接口。
 * 负责将 {@link ApplicationEvent} 广播给所有已注册的 {@link ApplicationListener}。
 * 广播器不关心事件的具体来源，只负责分发到合适的监听器。
 */
public interface ApplicationEventMulticaster {

    /**
     * 添加事件监听器，使其能接收所有事件通知。
     */
    void addApplicationListener(ApplicationListener<?> listener);

    /**
     * 移除事件监听器，使其不再接收事件通知。
     */
    void removeApplicationListener(ApplicationListener<?> listener);

    /**
     * 将事件广播给所有匹配的监听器。
     */
    void multicastEvent(ApplicationEvent event);

}

