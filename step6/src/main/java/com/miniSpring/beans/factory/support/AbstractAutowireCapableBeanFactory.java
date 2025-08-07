package com.miniSpring.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.PropertyValue;
import com.miniSpring.beans.PropertyValues;
import com.miniSpring.beans.factory.config.AutowireCapableBeanFactory;
import com.miniSpring.beans.factory.config.BeanDefinition;
import com.miniSpring.beans.factory.config.BeanPostProcessor;
import com.miniSpring.beans.factory.config.BeanReference;

import java.lang.reflect.Constructor;
//Bean 实例化、属性注入、初始化、销毁等生命周期管理的核心实现类。
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {
    //定义了一个创建对象的实例化策略属性类 InstantiationStrategy instantiationStrategy，这里我们选择了 Cglib 的实现类
    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;
        try {
            //创建实例
            bean = createBeanInstance(beanDefinition, beanName, args);
            //注入属性
            applyPropertyValues(bean, beanName, beanDefinition);
            // 执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed", e);
        }

        addSingleton(beanName, bean);
        return bean;
    }

    /**
     *初始化 Bean：执行初始化前后处理器、调用初始化方法invokeInitMethods
     */
    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 1. 执行 BeanPostProcessor Before 处理
        //    比如：@PostConstruct 或 Aware 接口处理可以在这里插入
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        // 待完成内容：invokeInitMethods(beanName, wrappedBean, beanDefinition);
        invokeInitMethods(beanName, wrappedBean, beanDefinition);

        // 2. 执行 BeanPostProcessor After 处理
        //    比如：@PostConstruct 或 Aware 接口处理可以在这里插入
        wrappedBean = applyBeanPostProcessorsAfterInitialization(bean, beanName);

        // 返回最终初始化完成的 Bean
        return wrappedBean;
    }

    /**
     * 调用 Bean 的初始化方法
     */
    private void invokeInitMethods(String beanName, Object wrappedBean, BeanDefinition beanDefinition) {
        //TODO：后续补充
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (null == current) return result;
            result = current;
        }
        return result;
    }

    /*
     * 执行所有注册的 BeanPostProcessor 的 postProcessBeforeInitialization 方法
     * 用于在 Bean 初始化方法执行前，做一些预处理操作
     */
    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (null == current) return result;
            result = current;
        }
        return result;
    }


    /*
     * 执行所有注册的 BeanPostProcessor 的 postProcessAfterInitialization 方法
     * 用于在 Bean 初始化方法执行后，做一些扩展操作（比如 AOP 动态代理等）
     */
    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) {
        Constructor constructorToUse = null;
        Class beanClass = beanDefinition.getBeanClass();
        //通过 beanClass.getDeclaredConstructors() 方式可以获取到你所有的构造函数，是一个集合
        Constructor[] declaredConstructors = beanClass.getDeclaredConstructors();

        //循环比对匹配构造函数与args，此处简化为数量对比，实际还需比对入参类型
        for (Constructor ctor : declaredConstructors) {
            if (null != args && ctor.getParameterTypes().length == args.length) {
                constructorToUse = ctor;
                break;
            }
        }

        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructorToUse, args);
    }

    private void applyPropertyValues(Object bean, String beanName, BeanDefinition beanDefinition) {

        //从beanDefinition得到这个bean的propertyValues。依次注入。如果要注入的属性时另一个bean，就getBean（创建或获取）。
        try {
            PropertyValues propertyValues = beanDefinition.getPropertyValues();
            for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {

                String name = propertyValue.getName();
                Object value = propertyValue.getValue();

                //instanceof：判断value这个对象是否为BeanReference的实例
                if (value instanceof BeanReference) {

                    //注意暂时没有去处理循环依赖的问题，后续补充
                    BeanReference beanReference = (BeanReference) value;
                    value = getBean(beanReference.getBeanName());
                }

                // 属性填充(BeanUtil是hutool的)
                BeanUtil.setFieldValue(bean, name, value);
            }
        } catch (Exception e) {
            throw new BeansException("Error setting property values：" + beanName);
        }
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

}