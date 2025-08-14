package com.miniSpring.beans.factory;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.PropertyValue;
import com.miniSpring.beans.PropertyValues;
import com.miniSpring.beans.factory.config.BeanDefinition;
import com.miniSpring.beans.factory.config.BeanFactoryPostProcessor;
import com.miniSpring.core.io.DefaultResourceLoader;
import com.miniSpring.core.io.Resource;
import com.miniSpring.util.StringValueResolver;

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
        try {
            // 1. 创建资源加载器，加载指定位置的属性文件
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(location);

            // 2. 读取属性文件内容到 Properties 对象中
            Properties properties = new Properties();
            properties.load(resource.getInputStream());

            // 3. 遍历容器中所有 BeanDefinition
            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            for (String beanName : beanDefinitionNames) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

                // 4. 获取该 BeanDefinition 的所有属性值
                PropertyValues propertyValues = beanDefinition.getPropertyValues();
                for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                    Object value = propertyValue.getValue();

                    // 5. 只处理字符串类型的属性值
                    if (!(value instanceof String)) continue;

                    // 6. 替换属性值中的占位符（如 ${xxx}）
                    value = resolvePlaceholder((String) value, properties);

                    // 7. 将替换后的值重新写回到属性列表
                    propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(), value));
                }
            }

            // 8. 向 BeanFactory 注册一个字符串解析器
            //    这个解析器会在解析 @Value 注解时生效，用于处理占位符
            StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(properties);
            beanFactory.addEmbeddedValueResolver(valueResolver);

        } catch (IOException e) {
            // 9. 属性文件加载失败时，抛出 BeansException
            throw new BeansException("Could not load properties", e);
        }
    }


    /**
     * 解析字符串中的占位符并替换为对应的属性值。
     * 占位符格式为 `${key}`，从给定的 Properties 中获取 key 对应的值进行替换。
     * 如果字符串中不存在占位符，则返回原字符串。
     */
    private String resolvePlaceholder(String value, Properties properties) {
        String strVal = value;
        StringBuilder buffer = new StringBuilder(strVal);
        int startIdx = strVal.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
        int stopIdx = strVal.indexOf(DEFAULT_PLACEHOLDER_SUFFIX);
        if (startIdx != -1 && stopIdx != -1 && startIdx < stopIdx) {
            String propKey = strVal.substring(startIdx + 2, stopIdx);
            String propVal = properties.getProperty(propKey);
            buffer.replace(startIdx, stopIdx + 1, propVal);
        }
        return buffer.toString();
    }


    /**
     * 设置属性文件的位置
     * @param location 属性文件的路径
     */
    public void setLocation(String location) {
        this.location = location;
    }

    private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final Properties properties;

        public PlaceholderResolvingStringValueResolver(Properties properties) {
            this.properties = properties;
        }

        @Override
        public String resolveStringValue(String strVal) {
            return PropertyPlaceholderConfigurer.this.resolvePlaceholder(strVal, properties);
        }

    }

}