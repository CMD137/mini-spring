package com.miniSpring.beans.factory;

/**
 * 标记超接口，表明某个 Bean 有资格通过回调方法
 * 被 Spring 容器通知特定的框架对象。
 * 具体的方法签名由各个子接口定义，
 * 通常是一个返回 void、接受单个参数的方法。
 *
 * 标记接口，实现该接口的 Bean 可以被 Spring 容器感知。
 */

public interface Aware {
}
