package com.miniSpring.beans.factory.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.DisposableBean;
import com.miniSpring.beans.factory.ObjectFactory;
import com.miniSpring.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    // 一级缓存，普通对象
    protected Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    // 二级缓存，提前暴漏对象，没有完全实例化的对象
    protected final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>();

    // 三级缓存，存放代理对象
    protected final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<String, ObjectFactory<?>>();

    //注册了销毁回调的 Bean 对象的容器
    private final Map<String, DisposableBean> disposableBeans = new LinkedHashMap<>();


    /** 缓存中用于表示 null 值的特殊标记对象，避免存储 null 导致异常 */
    protected static final Object NULL_OBJECT = new Object();

    /**
     * 销毁所有单例 Bean 对象。
     *
     * 该方法会遍历容器中所有已注册的实现了 DisposableBean 接口的 Bean（存储在 disposableBeans 中），
     * 并按注册顺序的逆序依次调用它们的 destroy() 方法完成销毁。
     *
     * 逆序销毁是为了保证依赖关系正确释放（先销毁依赖者，再销毁被依赖者）。
     *
     * 销毁过程中如果发生异常，会包装成 BeansException 并抛出，确保调用者能感知销毁失败。
     */
    public void destroySingletons() {
        Set<String> keySet = this.disposableBeans.keySet();
        Object[] disposableBeanNames = keySet.toArray();

        //i倒序遍历：逆序销毁时因为被加入容器的顺序就暗含了依赖顺序
        for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
            Object beanName = disposableBeanNames[i];
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new BeansException("Destroy method on bean with name '" + beanName + "' threw an exception", e);
            }
        }
    }


    @Override
    public Object getSingleton(String beanName) {
        // 1. 优先从一级缓存获取（完全初始化的单例对象）
        Object singletonObject = singletonObjects.get(beanName);
        if (null == singletonObject) {
            // 2. 一级缓存没有，则从二级缓存获取（半成品对象/代理对象）
            singletonObject = earlySingletonObjects.get(beanName);
            if (null == singletonObject) {
                // 3. 二级缓存也没有，则从三级缓存获取（ObjectFactory 提前暴露的对象工厂）
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    // 通过工厂创建对象（可能是代理对象）
                    singletonObject = singletonFactory.getObject();
                    // 放入二级缓存，并移除三级缓存
                    earlySingletonObjects.put(beanName, singletonObject);
                    singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    /**
     * 缓存转移
     * 注册单例对象到一级缓存，同时清理二、三级缓存
     */
    public void registerSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
    }

    /**
     * 向三级缓存添加对象工厂，用于提前暴露对象
     */
    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory){
        if (!this.singletonObjects.containsKey(beanName)) {
            // 如果一级缓存中没有成品对象
            // 1. 将对象工厂放入三级缓存（用于生成代理或提前引用）
            this.singletonFactories.put(beanName, singletonFactory);
            // 2. 移除二级缓存中的半成品对象（优先保证代理对象生效）
            this.earlySingletonObjects.remove(beanName);
        }

    }

    /**
     * 注册需要销毁的 Bean（容器关闭时调用 destroy）
     */
    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }

}
