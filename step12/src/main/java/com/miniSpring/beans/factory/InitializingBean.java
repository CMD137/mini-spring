package com.miniSpring.beans.factory;

// 初始化接口
public interface InitializingBean {
    /**
     * Bean 处理了属性填充后调用
     */
    void afterPropertiesSet() throws Exception;
}
