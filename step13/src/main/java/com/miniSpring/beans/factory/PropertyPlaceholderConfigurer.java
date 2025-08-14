package com.miniSpring.beans.factory;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.PropertyValue;
import com.miniSpring.beans.PropertyValues;
import com.miniSpring.beans.factory.config.BeanDefinition;
import com.miniSpring.beans.factory.config.BeanFactoryPostProcessor;
import com.miniSpring.core.io.DefaultResourceLoader;
import com.miniSpring.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * 该类实现了Spring的BeanFactoryPostProcessor接口，
 * 用于处理Bean定义中的属性占位符，将${...}形式的占位符替换为属性文件中的实际值
 */
public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {

    /**
     * 默认的占位符前缀: {@value}
     */
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    /**
     * 默认的占位符后缀: {@value}
     */
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    // 属性文件的位置
    private String location;

    /**
     * 实现BeanFactoryPostProcessor接口的方法，在BeanFactory加载完所有Bean定义后执行
     * 用于替换Bean定义中的属性占位符
     * @param beanFactory 可配置的Bean工厂，用于获取和修改Bean定义
     * @throws BeansException 如果处理过程中发生错误
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 加载属性文件并替换占位符
        try {
            // 创建默认的资源加载器，用于加载属性文件
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
            // 根据配置的位置加载资源
            Resource resource = resourceLoader.getResource(location);
            // 创建Properties对象并加载资源中的属性
            Properties properties = new Properties();
            properties.load(resource.getInputStream());

            // 获取所有Bean定义的名称
            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            // 遍历每个Bean定义
            for (String beanName : beanDefinitionNames) {
                // 获取当前Bean的定义信息
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

                // 获取Bean定义中的所有属性值
                PropertyValues propertyValues = beanDefinition.getPropertyValues();
                // 遍历每个属性值
                for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                    // 获取属性值对象
                    Object value = propertyValue.getValue();
                    // 如果属性值不是字符串类型，则跳过
                    if (!(value instanceof String)) continue;
                    String strVal = (String) value;

                    // 查找占位符前缀的位置
                    int startIdx = strVal.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
                    // 查找占位符后缀的位置
                    int stopIdx = strVal.indexOf(DEFAULT_PLACEHOLDER_SUFFIX);

                    // 如果找到了有效的占位符（前缀在后缀之前）
                    if (startIdx != -1 && stopIdx != -1 && startIdx < stopIdx) {
                        // 提取占位符中的属性键（去掉前缀和后缀）
                        String propKey = strVal.substring(startIdx + 2, stopIdx);
                        // 从属性文件中获取对应的属性值
                        String propVal = properties.getProperty(propKey);
                        // 替换占位符为实际属性值
                        String newValue = strVal.replace(DEFAULT_PLACEHOLDER_PREFIX + propKey + DEFAULT_PLACEHOLDER_SUFFIX, propVal);
                        // 更新Bean定义中的属性值
                        propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), newValue));
                    }
                }
            }
        } catch (IOException e) {
            // 如果加载属性文件失败，抛出异常
            throw new BeansException("Could not load properties", e);
        }
    }

    /**
     * 设置属性文件的位置
     * @param location 属性文件的路径
     */
    public void setLocation(String location) {
        this.location = location;
    }

}