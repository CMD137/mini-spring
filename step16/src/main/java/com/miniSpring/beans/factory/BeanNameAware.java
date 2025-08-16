package com.miniSpring.beans.factory;

/**
 * 使 Bean 能感知其在容器中的名称。
 * 实现该接口的 Bean 在初始化时会被注入 Bean 名称。
 */
public interface BeanNameAware extends Aware {

    void setBeanName(String name);

}
