package com.miniSpring.beans.factory.support;

import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.DisposableBean;
import com.miniSpring.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    //单例对象池
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    //注册了销毁回调的 Bean 对象的容器
    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();

    /** 缓存中用于表示 null 值的特殊标记对象，避免存储 null 导致异常 */
    protected static final Object NULL_OBJECT = new Object();

    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }

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
        return singletonObjects.get(beanName);
    }
    protected void addSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }
}
