package com.miniSpring.beans.factory;
/**
 * FactoryBean 是 Spring 提供的一种特殊 Bean 接口，
 * 用于自定义复杂对象的创建逻辑。
 *
 * 它的作用是：当一个 Bean 实现了 FactoryBean 接口，
 * Spring 容器在获取该 Bean 时，返回的是 getObject() 方法
 * 生产的对象（称为“产物对象”），而不是 FactoryBean 本身。
 * 如果需要获取工厂对象本身，可以在 getBean 时在名称前加 "&" 前缀。
 */
public interface FactoryBean<T> {

    T getObject() throws Exception;

    Class<?> getObjectType();

    boolean isSingleton();

}

