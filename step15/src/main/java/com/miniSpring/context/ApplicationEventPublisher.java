package com.miniSpring.context;

/**
 * 应用事件发布器接口。
 * 负责向所有已注册的监听器发布应用事件。
 * 事件可以是框架内部事件，也可以是应用自定义事件。
 */
public interface ApplicationEventPublisher {

    /**
     * 发布事件，通知所有监听该事件的监听器。
     *
     * @param event 要发布的事件对象
     */
    void publishEvent(ApplicationEvent event);

}

