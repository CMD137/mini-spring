package com.miniSpring.core.io;

/**
 * 资源加载器接口，定义统一的资源加载方式。
 * 框架中所有资源（如 XML、配置文件等）都应通过该接口加载。
 */
public interface ResourceLoader {

    /**
     * 类路径前缀，用于识别类路径资源。
     * 例如：classpath:application.xml
     */
    String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * 根据给定路径 location 返回对应的 Resource 实例。
     * 路径可以是：
     * - classpath:xxx.xml（类路径）
     * - file:/xxx/xxx.txt（文件系统）
     * - http://xxx.com/xx.properties（网络地址）
     *
     * @param location 资源位置字符串
     * @return 封装后的 Resource 对象（可进一步读取 InputStream）
     */
    Resource getResource(String location);
}

