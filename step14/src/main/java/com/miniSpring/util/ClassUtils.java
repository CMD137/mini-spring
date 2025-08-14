package com.miniSpring.util;

/**
 * 类工具类，用于获取默认的类加载器。
 * 在不同的运行环境下，使用合适的类加载器是框架设计中很重要的一环。
 */
public class ClassUtils {

    /**
     * 获取默认的类加载器
     * @return 当前线程的上下文类加载器，如果获取不到，则返回加载本类的类加载器
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;

        try {
            // 尝试获取当前线程上下文的类加载器（最常用、最灵活）
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // 获取失败时捕获异常（比如安全沙箱环境会抛异常）
            // 此处不处理异常，只是 fallback 到下一个方案
        }

        if (cl == null) {
            // 如果线程上下文类加载器为 null，则使用当前类（ClassUtils）自身的类加载器
            cl = ClassUtils.class.getClassLoader();
        }

        // 返回获取到的类加载器
        return cl;
    }

    /**
     * 判断指定类是否为 CGLIB 动态代理生成的类。
     *
     * @param clazz 要检查的类对象
     * @return 如果是 CGLIB 代理类返回 true，否则返回 false
     */
    public static boolean isCglibProxyClass(Class<?> clazz) {
        return (clazz != null && isCglibProxyClassName(clazz.getName()));
    }

    /**
     * 判断指定类名是否为 CGLIB 动态代理生成的类名。
     *
     * @param className 要检查的类名字符串
     * @return 如果类名包含 CGLIB 代理标识“$$”返回 true，否则返回 false
     */
    public static boolean isCglibProxyClassName(String className) {
        return (className != null && className.contains("$$"));
    }


}

