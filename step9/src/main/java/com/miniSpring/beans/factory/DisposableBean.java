package com.miniSpring.beans.factory;

// 销毁接口
public interface DisposableBean {
    // 销毁方法
    void destroy() throws Exception;

}
