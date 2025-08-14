package com.miniSpring.aop.framework;

import com.miniSpring.aop.AdvisedSupport;
import com.miniSpring.aop.Advisor;
import com.miniSpring.aop.MethodBeforeAdvice;
import com.miniSpring.aop.PointcutAdvisor;
import com.miniSpring.aop.adapter.MethodBeforeAdviceInterceptor;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Cglib2AopProxy implements AopProxy {

    private final AdvisedSupport advised;

    public Cglib2AopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(advised.getTargetSource().getTarget().getClass());
        enhancer.setInterfaces(advised.getTargetSource().getTargetInterfaces());
        enhancer.setCallback(new DynamicAdvisedInterceptor(advised));
        return enhancer.create();
    }

    /**
     * CGLIB 的 MethodInterceptor
     * 负责拦截目标对象的方法调用，执行拦截器链或直接调用目标方法
     */
    private static class DynamicAdvisedInterceptor implements MethodInterceptor {

        private final AdvisedSupport advised;

        public DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            // 1. 检查是否是Object类的方法（如toString、hashCode等），这些方法通常不需要增强
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(advised.getTargetSource().getTarget(), args);
            }


            // 2. 获取匹配的Advisor
            List<PointcutAdvisor> eligibleAdvisors = new ArrayList<>();
            for (PointcutAdvisor advisor : advised.getAdvisors()) {
                if (advisor.getPointcut().getMethodMatcher()
                        .matches(method, advised.getTargetSource().getTarget().getClass())) {
                    eligibleAdvisors.add(advisor);
                }
            }

            // 3. 把Advisor转换成MethodInterceptor链
            List<org.aopalliance.intercept.MethodInterceptor> interceptorChain = new ArrayList<>();
            for (Advisor advisor : eligibleAdvisors) {
                // 获取当前Advisor对应的Advice（增强逻辑）
                Advice advice = advisor.getAdvice();

                if (advice instanceof org.aopalliance.intercept.MethodInterceptor) {
                    // 如果Advice本身就是MethodInterceptor，直接加入拦截器链
                    interceptorChain.add((org.aopalliance.intercept.MethodInterceptor) advice);
                } else if (advice instanceof MethodBeforeAdvice) {
                    // 如果是前置通知接口，使用MethodBeforeAdviceInterceptor适配成MethodInterceptor
                    interceptorChain.add(new MethodBeforeAdviceInterceptor((MethodBeforeAdvice) advice));
                } else {
                    // 目前不支持的Advice类型，抛出异常提示
                    throw new IllegalArgumentException("Unsupported advice type: " + advice.getClass());
                }
            }

                // 执行拦截器链，传入自定义的 CglibMethodInvocation
                CglibMethodInvocation invocation = new CglibMethodInvocation(
                        advised.getTargetSource().getTarget(),
                        method,
                        args,
                        methodProxy,
                        interceptorChain
                );
                return invocation.proceed();
        }
    }

    /**
     * CglibMethodInvocation 继承 ReflectiveMethodInvocation，复用拦截器链调用逻辑，
     * 并重写 proceed() 用 CGLIB 的 methodProxy 来调用目标方法，避免反射调用。
     */
    private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

        private final MethodProxy methodProxy;

        public CglibMethodInvocation(Object target, Method method, Object[] arguments,
                                     MethodProxy methodProxy, List<org.aopalliance.intercept.MethodInterceptor> interceptors) {
            super(target, method, arguments, interceptors);
            this.methodProxy = methodProxy;
        }

        @Override
        public Object proceed() throws Throwable {
            if (currentInterceptorIndex == methodInterceptorList.size() - 1) {
                // 所有拦截器执行完后，使用 CGLIB 方式调用目标方法，效率更高
                return methodProxy.invoke(target, arguments);
            }
            currentInterceptorIndex++;
            org.aopalliance.intercept.MethodInterceptor interceptor = methodInterceptorList.get(currentInterceptorIndex);
            return interceptor.invoke(this);
        }
    }
}
