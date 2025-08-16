package com.miniSpring.beans.factory.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.ConfigurableListableBeanFactory;
import com.miniSpring.beans.factory.config.BeanDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry, ConfigurableListableBeanFactory {
    //bean注册表
    private Map<String, BeanDefinition> beanDefinitionMap =new ConcurrentHashMap<>();

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null){
            throw new BeansException("No bean named '" + beanName + "' is defined");
        }
        return beanDefinition;
    }

    @Override
    public void preInstantiateSingletons() throws BeansException {
        for (String beanName : beanDefinitionMap.keySet()) {
            getBean(beanName);
        }
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinitionMap.containsKey(beanName)){
            throw new BeansException(beanName + "already exists");
        }
        beanDefinitionMap.put(beanName,beanDefinition);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    /**
     * 根据指定类型获取所有匹配的Bean实例映射
     *
     * @param type 要获取的Bean类型
     * @param <T>  类型参数，限定返回值的类型
     * @return 包含所有匹配类型Bean的Map，键为Bean名称，值为Bean实例
     * @throws BeansException 当获取Bean过程中发生异常时抛出
     */
    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        // 创建用于存储结果的Map
        Map<String, T> result = new HashMap<>();

        // 遍历所有已注册的Bean定义
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            // 获取当前Bean定义对应的Class对象
            Class beanClass = beanDefinition.getBeanClass();

            // 判断当前Bean的类型是否与目标类型兼容
            // isAssignableFrom()方法用于判断当前Class对象所表示的类或接口是否与指定的Class参数表示的类或接口相同，或是否是其超类/超接口
            // 即：type是否是beanClass的父类/父接口，或者两者是同一个类
            if (type.isAssignableFrom(beanClass)) {
                result.put(beanName, (T) getBean(beanName));
            }
        });

        return result;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        List<String> beanNames = new ArrayList<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            Class beanClass = entry.getValue().getBeanClass();
            if (requiredType.isAssignableFrom(beanClass)) {
                beanNames.add(entry.getKey());
            }
        }
        if (1 == beanNames.size()) {
            return getBean(beanNames.get(0), requiredType);
        }

        throw new BeansException(requiredType + "expected single bean but found " + beanNames.size() + ": " + beanNames);
    }
}
