package com.miniSpring.context;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.Aware;

/**
 * 使 Bean 能感知所属的 ApplicationContext。
 * 实现该接口的 Bean 在初始化时会被注入当前 ApplicationContext。
 */
public interface ApplicationContextAware extends Aware {

    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;

}

