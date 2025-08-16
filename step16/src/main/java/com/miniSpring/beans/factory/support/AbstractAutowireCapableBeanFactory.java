package com.miniSpring.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.PropertyValue;
import com.miniSpring.beans.PropertyValues;
import com.miniSpring.beans.factory.*;
import com.miniSpring.beans.factory.config.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

//Bean 实例化、属性注入、初始化、销毁等生命周期管理的核心实现类。
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {
    //定义了一个创建对象的实例化策略属性类 InstantiationStrategy instantiationStrategy，这里我们选择了 Cglib 的实现类
    private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {

        Object bean = null;

        try {
            // 提前代理，提供一个机会来返回一个自定义的 bean 实例，从而跳过 Spring 默认的 bean 实例化、属性注入等流程。
            bean = resolveBeforeInstantiation(beanName, beanDefinition);
            if (null != bean) {
                return bean;
            }
        } catch (Exception e) {
            throw new BeansException("Failed during resolveBeforeInstantiation for bean: " + beanName, e);
        }

        try {
            // 创建实例
            bean = createBeanInstance(beanDefinition, beanName, args);
        } catch (Exception e) {
            throw new BeansException("Failed to create bean instance for: " + beanName, e);
        }

        try {
            // 在设置 Bean 属性之前，允许 AutowiredAnnotationBeanPostProcessor 修改属性值(@Autowired、@Value)
            applyBeanPostProcessorsBeforeApplyingPropertyValues(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Failed to process @Autowired/@Value annotations for bean: " + beanName, e);
        }

        try {
            // 注入属性
            applyPropertyValues(bean, beanName, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Failed to apply property values for bean: " + beanName, e);
        }

        try {
            // 执行 Bean 的初始化方法和 BeanPostProcessor 的前置和后置处理方法
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Failed to initialize bean: " + beanName, e);
        }


        //注册可销毁的 Bean
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        // 判断 SCOPE_SINGLETON、SCOPE_PROTOTYPE
        Object exposedObject = bean;
        if (beanDefinition.isSingleton()) {
            // 获取代理对象
            exposedObject = getSingleton(beanName);
            registerSingleton(beanName, exposedObject);
        }
        return exposedObject;
    }

    /**
     * 初始化 Bean 的过程：
     * 1. 调用实现了 Aware 接口的回调方法，注入相应的容器资源。
     * 2. 执行所有 BeanPostProcessor 的 postProcessBeforeInitialization 方法。
     * 3. 调用 Bean 自定义的初始化方法（如 afterPropertiesSet 或配置的 init-method）。
     * 4. 执行所有 BeanPostProcessor 的 postProcessAfterInitialization 方法。
     */
    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {

        // 调用 Aware 接口的回调方法，注入容器资源。（具体实现由Bean自己实现）
        if (bean instanceof Aware) {
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory(this);
            }
            if (bean instanceof BeanClassLoaderAware) {
                ((BeanClassLoaderAware) bean).setBeanClassLoader(getBeanClassLoader());
            }
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
        }

        // 执行 BeanPostProcessor 的初始化前置处理
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

        // 调用 Bean 的初始化方法（afterPropertiesSet、init-method）
        try {
            invokeInitMethods(beanName, wrappedBean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException("Invocation of init method of bean[" + beanName + "] failed", e);
        }

        // 执行 BeanPostProcessor 的初始化后置处理
        wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);

        return wrappedBean;
    }


    /**
     * 调用 Bean 的初始化方法
     */
    private void invokeInitMethods(String beanName, Object wrappedBean, BeanDefinition beanDefinition) throws Exception {
        // 1. 实现接口 InitializingBean
        if (wrappedBean instanceof InitializingBean) {
            ((InitializingBean) wrappedBean).afterPropertiesSet();
        }

        // 2. 配置信息 init-method
        String initMethodName = beanDefinition.getInitMethodName();
        if (StrUtil.isNotEmpty(initMethodName)) {
            Method initMethod = beanDefinition.getBeanClass().getMethod(initMethodName);
            if (null == initMethod) {
                throw new BeansException("Could not find an init method named '" + initMethodName + "' on bean with name '" + beanName + "'");
            }
            initMethod.invoke(wrappedBean);
        }
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
            throw new BeansException("Error setting property values：" + beanName,e);
        }
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

    /**
     * 若 Bean 实现了 DisposableBean 或配置了销毁方法，则注册其销毁适配器。
     */
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 非 Singleton 类型的 Bean 不执行销毁方法
        if (!beanDefinition.isSingleton()) return;

        if (bean instanceof DisposableBean || StrUtil.isNotEmpty(beanDefinition.getDestroyMethodName())) {
            // 使用 DisposableBeanAdapter 适配器包装 Bean
            // 适配器实现了 DisposableBean 接口，
            // 这样无论 Bean 是实现接口销毁，还是通过配置销毁方法，
            // 都能统一以 DisposableBean 形式注册到容器，方便统一管理和调用销毁逻辑。
            registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));

        }
    }

    /**
     * 在 Bean 实例化前调用，尝试通过 InstantiationAwareBeanPostProcessor
     * 返回代理对象，若有则跳过默认实例化。
     * 返回非空对象时，还会执行后置初始化处理。
     */
    protected Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition) {
        // 调用所有 InstantiationAwareBeanPostProcessor，尝试返回代理或替代对象
        Object bean = applyBeanPostProcessorBeforeInstantiation(beanDefinition.getBeanClass(), beanName);

        if (bean != null) {
            // 如果前置处理返回了代理对象，继续调用所有 BeanPostProcessor 的
            // postProcessAfterInitialization，保证代理对象完成生命周期的后置处理
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        }

        // 返回代理对象（非空）或 null（继续默认实例化）
        return bean;
    }

    /**
     * 遍历所有 BeanPostProcessor，调用实现了 InstantiationAwareBeanPostProcessor
     * 的 postProcessBeforeInstantiation，返回第一个非空结果。
     */
    public Object applyBeanPostProcessorBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        // 遍历所有注册的 BeanPostProcessor
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            // 过滤出实现了 InstantiationAwareBeanPostProcessor 的处理器
            if (processor instanceof InstantiationAwareBeanPostProcessor) {
                // 调用 postProcessBeforeInstantiation 方法尝试返回代理对象或替代 Bean
                Object result = ((InstantiationAwareBeanPostProcessor) processor).postProcessBeforeInstantiation(beanClass, beanName);
                // 如果返回非空对象，表示已创建代理或替代对象，直接返回，跳过默认实例化
                if (result != null) return result;
            }
        }
        // 如果所有处理器都未返回代理，返回 null，继续默认实例化流程
        return null;
    }

    /**
     * 在为 Bean 填充属性（依赖注入）之前，允许 BeanPostProcessor 对属性值进行修改。
     *
     * 这里主要是为了支持类似 @Autowired、@Value、@Resource 等注解驱动的依赖注入，
     * 因为它们依赖于 InstantiationAwareBeanPostProcessor 在属性设置前进行解析与赋值。
     *
     * @param beanName        当前 Bean 的名称
     * @param bean            已经实例化好的 Bean 对象（构造方法已调用，但属性还未注入）
     * @param beanDefinition  Bean 对应的定义信息（包含属性、构造参数等）
     */
    protected void applyBeanPostProcessorsBeforeApplyingPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {

        // 遍历当前容器中注册的所有 BeanPostProcessor
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            // 只有 InstantiationAwareBeanPostProcessor 才能在属性设置前做处理
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                // 调用其 postProcessPropertyValues 方法，让它有机会修改 Bean 的属性值
                // 例如 AutowiredAnnotationBeanPostProcessor 会在这里解析 @Autowired 并生成对应的依赖注入 PropertyValue
                PropertyValues pvs = ((InstantiationAwareBeanPostProcessor) beanPostProcessor)
                        .postProcessPropertyValues(beanDefinition.getPropertyValues(), bean, beanName);

                // 如果处理器返回了新的属性值集合，则合并到 BeanDefinition 的属性列表中
                if (null != pvs) {
                    for (PropertyValue propertyValue : pvs.getPropertyValues()) {
                        beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
                    }
                }
            }
        }
    }


}