package com.miniSpring.context;

import com.miniSpring.beans.factory.HierarchicalBeanFactory;
import com.miniSpring.beans.factory.ListableBeanFactory;
import com.miniSpring.core.io.ResourceLoader;

/**
 * ApplicationContext 为容器的核心接口（Central接口），负责统一对外提供容器功能。
 */
public interface ApplicationContext extends ListableBeanFactory, HierarchicalBeanFactory, ResourceLoader, ApplicationEventPublisher {
}

