package com.miniSpring.beans.factory.annotation;

import cn.hutool.core.bean.BeanUtil;
import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.PropertyValues;
import com.miniSpring.beans.factory.BeanFactory;
import com.miniSpring.beans.factory.BeanFactoryAware;
import com.miniSpring.beans.factory.ConfigurableListableBeanFactory;
import com.miniSpring.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.miniSpring.util.ClassUtils;

import java.lang.reflect.Field;

/**
 * 处理 @Value、@Autowired、@Qualifier 注解的 Bean 后置处理器。
 * <p>在 Bean 实例化后、属性赋值阶段，对字段进行依赖注入或配置值注入。
 */
public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    // Bean 工厂引用，用于获取 Bean 和解析嵌入值
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        // 1. 获取原始类（如果是 CGLIB 代理类则取父类）
        Class<?> clazz = bean.getClass();
        clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;

        Field[] declaredFields = clazz.getDeclaredFields();

        // 2. 处理 @Value 注解：注入外部配置值
        for (Field field : declaredFields) {
            Value valueAnnotation = field.getAnnotation(Value.class);
            if (null != valueAnnotation) {
                // 获取注解中的值（可能包含占位符）
                String value = valueAnnotation.value();
                // 解析占位符或表达式，得到实际值
                value = beanFactory.resolveEmbeddedValue(value);
                // 通过反射设置字段值
                BeanUtil.setFieldValue(bean, field.getName(), value);
            }
        }

        // 3. 处理 @Autowired 注解：注入 Bean 对象
        for (Field field : declaredFields) {
            Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
            if (null != autowiredAnnotation) {
                Class<?> fieldType = field.getType();
                String dependentBeanName = null;
                Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                Object dependentBean;

                if (null != qualifierAnnotation) {
                    // 如果有 @Qualifier，则按指定 Bean 名称 + 类型注入
                    dependentBeanName = qualifierAnnotation.value();
                    dependentBean = beanFactory.getBean(dependentBeanName, fieldType);
                } else {
                    // 否则按类型注入
                    dependentBean = beanFactory.getBean(fieldType);
                }
                // 通过反射设置字段值
                BeanUtil.setFieldValue(bean, field.getName(), dependentBean);
            }
        }

        // 返回原始属性值对象（本方法不修改 pvs）
        return pvs;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null; // 此处未实现，直接返回 null
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return null; // 初始化前的处理，这里未做额外逻辑
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null; // 初始化后的处理，这里未做额外逻辑
    }
}

