package com.miniSpring.aop.framework.autoproxy;

import com.miniSpring.aop.*;
import com.miniSpring.aop.aspectj.AspectJExpressionPointcutAdvisor;
import com.miniSpring.aop.framework.ProxyFactory;
import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.PropertyValues;
import com.miniSpring.beans.factory.BeanFactory;
import com.miniSpring.beans.factory.BeanFactoryAware;
import com.miniSpring.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.miniSpring.beans.factory.support.DefaultListableBeanFactory;
import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        Collection<AspectJExpressionPointcutAdvisor> advisorCollection =
                beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

        List<AspectJExpressionPointcutAdvisor> advisors = new ArrayList<>(advisorCollection);




        //直接用反射创建新对象，跳过 Spring 容器管理
            /*
            TargetSource targetSource = null;
            try {
                targetSource = new TargetSource(beanClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }

            */

        // 准备代理相关配置
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTargetSource(new TargetSource(bean));
        advisedSupport.setProxyTargetClass(bean.getClass().getInterfaces().length == 0);


        //debug:
        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            System.out.println("Advice类：" + advisor.getAdvice().getClass().getName());

            // 取切点的 MethodMatcher
            MethodMatcher mm = advisor.getPointcut().getMethodMatcher();

            // 假设目标类是 targetClass
            Class<?> targetClass = advisedSupport.getTargetSource().getTarget().getClass();

            // 遍历目标类所有方法，检查是否匹配切点
            for (Method method : targetClass.getMethods()) {
                if (mm.matches(method, targetClass)) {
                    System.out.println("匹配的方法名: " + method.getName());
                }
            }
        }

        // 遍历所有的切面通知器(Advisor)
        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            // 检查当前通知器的切入点是否匹配目标Bean的类
            // getClassFilter()获取类过滤器，matches()判断目标类是否符合切入点表达式
            if (advisor.getPointcut().getClassFilter().matches(bean.getClass())) {
                // 如果匹配，则将该通知器添加到通知支持类中
                advisedSupport.addAdvisor(advisor); // 存储匹配的通知器
            }
        }

        // 检查是否有匹配的通知器
        if (!advisedSupport.getAdvisors().isEmpty()) {
            // 如果有匹配的通知器，使用代理工厂创建代理对象并返回
            return new ProxyFactory(advisedSupport).getProxy();
        }
        // 如果没有匹配的通知器，直接返回原始Bean对象
        return bean;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        return pvs;
    }

}
