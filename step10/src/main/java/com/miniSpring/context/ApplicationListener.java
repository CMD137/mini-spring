package com.miniSpring.context;

import java.util.EventListener;

/**
 * 应用事件监听器接口，需由事件监听器实现。
 *  * 基于标准的 <code>java.util.EventListener</code> 接口，
 *  * 采用观察者设计模式（Observer Pattern）。
 */
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

    /**
     * 处理应用事件
     */
    void onApplicationEvent(E event);

}
