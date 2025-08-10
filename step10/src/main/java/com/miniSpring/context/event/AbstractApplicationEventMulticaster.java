package com.miniSpring.context.event;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.BeanFactory;
import com.miniSpring.beans.factory.BeanFactoryAware;
import com.miniSpring.context.ApplicationEvent;
import com.miniSpring.context.ApplicationListener;
import com.miniSpring.util.ClassUtils;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * 抽象应用事件广播器基类，实现了 ApplicationEventMulticaster 和 BeanFactoryAware。
 * 维护注册的事件监听器集合，负责筛选合适监听器以分发事件。
 * 支持基于泛型参数类型判断监听器是否对特定事件感兴趣。
 */
public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware {

    /**
     * 已注册的事件监听器集合，使用 LinkedHashSet 保证顺序且去重。
     */
    public final Set<ApplicationListener<ApplicationEvent>> applicationListeners = new LinkedHashSet<>();

    /**
     * Spring 容器 BeanFactory 引用，用于监听器或事件的依赖注入。
     */
    private BeanFactory beanFactory;

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.add((ApplicationListener<ApplicationEvent>) listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.remove(listener);
    }

    @Override
    public final void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 根据事件类型筛选出所有感兴趣的监听器。
     *
     * @param event 当前发布的事件
     * @return 支持该事件的监听器集合
     */
    protected Collection<ApplicationListener> getApplicationListeners(ApplicationEvent event) {
        LinkedList<ApplicationListener> allListeners = new LinkedList<>();
        // 遍历已注册监听器，判断是否支持该事件
        for (ApplicationListener<ApplicationEvent> listener : applicationListeners) {
            if (supportsEvent(listener, event)) {
                allListeners.add(listener);
            }
        }
        return allListeners;
    }

    /**
     * 判断监听器是否支持处理该事件。
     * 通过反射获取监听器泛型参数的实际类型，判断该类型是否为事件对象的超类或接口。
     *
     * @param applicationListener 事件监听器
     * @param event               事件对象
     * @return 如果监听器泛型类型能接收该事件，返回 true；否则 false。
     */
    protected boolean supportsEvent(ApplicationListener<ApplicationEvent> applicationListener, ApplicationEvent event) {
        // 获取监听器实现类
        Class<? extends ApplicationListener> listenerClass = applicationListener.getClass();

        // 判断是否为 CGLIB 代理类，是则获取其父类(代理类本身不包含泛型信息，需要获取其父类（即被代理的目标类）来准确获取泛型参数)
        Class<?> targetClass = ClassUtils.isCglibProxyClass(listenerClass) ? listenerClass.getSuperclass() : listenerClass;

        // 获取实现的第一个接口的泛型类型（通常为 ApplicationListener<E>）
        Type genericInterface = targetClass.getGenericInterfaces()[0];

        // 获取泛型参数 E 的实际类型
        Type actualTypeArgument = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
        String className = actualTypeArgument.getTypeName();

        Class<?> eventClassName;
        try {
            // 加载泛型参数对应的事件类
            eventClassName = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new BeansException("错误的事件类名: " + className);
        }

        // 判断事件类是否为泛型参数类或其子类（支持事件继承）
        return eventClassName.isAssignableFrom(event.getClass());
    }

}

