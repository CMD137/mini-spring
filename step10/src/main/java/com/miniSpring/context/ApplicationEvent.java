package com.miniSpring.context;

import java.util.EventObject;

/**
 * 具备事件功能的抽象类
 * 继承自 {@link java.util.EventObject}，封装了事件源对象。
 * 所有容器事件与自定义事件都应继承该类。
 */
public abstract class ApplicationEvent extends EventObject {

    /**
     * 构造事件对象。
     */
    public ApplicationEvent(Object source) {
        super(source);
    }

}
