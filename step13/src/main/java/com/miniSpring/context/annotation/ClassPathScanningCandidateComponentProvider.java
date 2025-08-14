package com.miniSpring.context.annotation;

import cn.hutool.core.util.ClassUtil;
import com.miniSpring.beans.factory.config.BeanDefinition;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 在基于注解的 Bean 注册机制中，需要有一个步骤能从指定包路径中发现符合条件的类，
 * 并将它们作为候选组件（Candidate Component）交给 BeanDefinition 注册流程处理。
 * 该类就是完成“扫描 + 转换”这一功能的核心工具类。
 */

public class ClassPathScanningCandidateComponentProvider {

    /**
     * 扫描指定基础包路径，查找被 {@link Component} 注解标记的类，
     * 并将其封装为 {@link BeanDefinition} 对象集合返回。
     *
     * @param basePackage 基础包路径（如 "com.example.service"）
     * @return 候选 BeanDefinition 集合
     */
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        // 用 LinkedHashSet 保证扫描结果有序且不重复
        Set<BeanDefinition> candidates = new LinkedHashSet<>();

        // 调用工具方法扫描出被 @Component 标记的类
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(basePackage, Component.class);

        // 将扫描到的类转换为 BeanDefinition 并加入集合
        for (Class<?> clazz : classes) {
            candidates.add(new BeanDefinition(clazz));
        }

        return candidates;
    }
}
