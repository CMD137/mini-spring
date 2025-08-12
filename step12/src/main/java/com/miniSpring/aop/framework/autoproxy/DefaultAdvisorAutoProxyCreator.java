package com.miniSpring.aop.framework.autoproxy;

import com.miniSpring.aop.*;
import com.miniSpring.aop.aspectj.AspectJExpressionPointcutAdvisor;
import com.miniSpring.aop.framework.ProxyFactory;
import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.factory.BeanFactory;
import com.miniSpring.beans.factory.BeanFactoryAware;
import com.miniSpring.beans.factory.support.DefaultListableBeanFactory;
import com.miniSpring.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.Collection;

/**
 * 自动代理创建器，自动扫描容器中所有的 AspectJExpressionPointcutAdvisor，
 * 根据切点匹配结果对目标Bean生成代理。
 *
 * 实现了 InstantiationAwareBeanPostProcessor，可以在 Bean 实例化前替换成代理对象。
 */
public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // 这里强转是为了方便后续调用 getBeansOfType 方法
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    /**
     * 在 Bean 实例化之前尝试返回代理对象。
     * @param beanClass 待创建Bean的类
     * @param beanName  Bean名称
     * @return 如果匹配切面返回代理，否则返回 null 继续创建原Bean
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        // 目前先直接返回null，后期处理循环依赖问题时才需要提前创建代理对象并暴露
        return null;
    }

    /**
     * 判断是否为Spring基础设施类，避免代理这些类。
     */
    private boolean isInfrastructureClass(Class<?> beanClass) {
        return Advice.class.isAssignableFrom(beanClass) ||
                Pointcut.class.isAssignableFrom(beanClass) ||
                Advisor.class.isAssignableFrom(beanClass);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 不做处理，直接返回原始bean
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 排除Spring自身基础设施类，防止循环代理
        if (isInfrastructureClass(bean.getClass())) {
            return bean; // 不代理，直接返回原对象
        }

        // 获取所有切面通知组合对象
        Collection<AspectJExpressionPointcutAdvisor> advisors =
                beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();



        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            // 判断切面切点是否匹配当前Bean类型
            ClassFilter classFilter = advisor.getPointcut().getClassFilter();
            if (!classFilter.matches(bean.getClass())) {
                continue;
            }

            // 准备代理相关配置
            AdvisedSupport advisedSupport = new AdvisedSupport();

            //直接用反射创建新对象，跳过 Spring 容器管理
            /*
            TargetSource targetSource = null;
            try {
                targetSource = new TargetSource(beanClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }

            */

//            // 通过beanFactory获取目标Bean实例（非重复创建）
//            Object target = beanFactory.getBean(beanName);
//            advisedSupport.setTargetSource(new TargetSource(target));
            // 直接使用当前Bean实例作为目标对象（正确做法）
            advisedSupport.setTargetSource(new TargetSource(bean));

            // 添加拦截器链，这里简单添加单个Advice的拦截器
            advisedSupport.addMethodInterceptor((MethodInterceptor) advisor.getAdvice());

            // 设置方法匹配器
            advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());

            // 是否使用Cglib代理（这里默认JDK代理）
            advisedSupport.setProxyTargetClass(false);

            // 通过代理工厂生成代理对象并返回
            return new ProxyFactory(advisedSupport).getProxy();
        }

        // 无匹配切面，返回原Bean
        return bean;
    }

}
