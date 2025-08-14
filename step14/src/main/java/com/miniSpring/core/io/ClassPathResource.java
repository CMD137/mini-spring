package com.miniSpring.core.io;

import cn.hutool.core.lang.Assert;
import com.miniSpring.util.ClassUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * ClassPathResource 实现了 Resource 接口，
 * 用于从 classpath（类路径）中加载资源，如配置文件。
 */
public class ClassPathResource implements Resource {

    // 资源路径，例如 "application.xml"
    private final String path;

    // 用于加载资源的类加载器
    private ClassLoader classLoader;

    /**
     * 构造函数，只传入路径，使用默认类加载器
     * @param path 类路径下的资源路径
     */
    public ClassPathResource(String path) {
        // 调用另一个构造函数，显式传入 null，表示使用默认类加载器
        // `(ClassLoader) null` 是一种语法写法，明确指定 null 是 ClassLoader 类型，防止类型不匹配
        this(path, (ClassLoader) null);
    }

    /**
     * 构造函数，允许指定类加载器
     * @param path 资源路径
     * @param classLoader 类加载器，可为空
     */
    public ClassPathResource(String path, ClassLoader classLoader) {
        // 使用 hutool 提供的断言工具，确保 path 不为 null
        // 否则会抛出 IllegalArgumentException，提示错误信息
        Assert.notNull(path, "Path must not be null");

        this.path = path;

        // 如果传入的 classLoader 为 null，则使用默认类加载器（由 ClassUtils 提供）
        this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
    }

    /**
     * 获取资源的输入流，供后续解析（例如 XML 配置解析）
     * @return InputStream 输入流
     * @throws IOException 如果找不到资源则抛出异常
     */
    @Override
    public InputStream getInputStream() throws IOException {
        // 使用类加载器从 classpath 中加载资源为输入流
        InputStream is = classLoader.getResourceAsStream(path);

        // 如果加载失败，返回 null，则抛出文件未找到异常
        if (is == null) {
            throw new FileNotFoundException(
                    this.path + " cannot be opened because it does not exist");
        }

        return is;
    }
}

