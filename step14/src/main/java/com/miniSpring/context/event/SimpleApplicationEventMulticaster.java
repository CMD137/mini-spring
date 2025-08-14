package com.miniSpring.context.event;

import com.miniSpring.beans.factory.BeanFactory;
import com.miniSpring.context.ApplicationEvent;
import com.miniSpring.context.ApplicationListener;

/**
 * 简单的应用事件广播器实现类，继承自 AbstractApplicationEventMulticaster。
 * 负责将事件同步地广播给所有匹配的监听器。
 * 通过构造方法注入 BeanFactory，实现监听器的依赖管理。
 *
 * 作用：
 * - 管理事件监听器的注册和筛选
 * - 实现事件的同步发布和通知机制
 */
public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

    public SimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void multicastEvent(final ApplicationEvent event) {
        // 遍历所有支持该事件的监听器，逐个同步调用其事件处理方法
        for (final ApplicationListener listener : getApplicationListeners(event)) {
            listener.onApplicationEvent(event);
        }
    }

}
