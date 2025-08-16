package com.miniSpring.aop.framework;

import com.miniSpring.aop.*;
import com.miniSpring.aop.adapter.MethodAfterAdviceInterceptor;
import com.miniSpring.aop.adapter.MethodAroundAdviceInterceptor;
import com.miniSpring.aop.adapter.MethodBeforeAdviceInterceptor;
import com.miniSpring.core.Ordered;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
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
        // 传入当前 Cglib2AopProxy 实例，供内部类调用 adaptAdviceToInterceptor
        enhancer.setCallback(new DynamicAdvisedInterceptor(advised, this));
        return enhancer.create();
    }

    /**
     * 将 Advice 转换成 MethodInterceptor 列表
     */
    public List<org.aopalliance.intercept.MethodInterceptor> adaptAdviceToInterceptor(Advice advice) {
        List<org.aopalliance.intercept.MethodInterceptor> interceptors = new ArrayList<>();

        if (advice instanceof MethodAroundAdvice) {
            interceptors.add(new MethodAroundAdviceInterceptor((MethodAroundAdvice) advice));
        } else if (advice instanceof MethodBeforeAdvice) {
            interceptors.add(new MethodBeforeAdviceInterceptor((MethodBeforeAdvice) advice));
        } else if (advice instanceof MethodAfterAdvice) {
            interceptors.add(new MethodAfterAdviceInterceptor((MethodAfterAdvice) advice));
        } else if (advice instanceof org.aopalliance.intercept.MethodInterceptor) {
            interceptors.add((org.aopalliance.intercept.MethodInterceptor) advice);
        } else {
            throw new IllegalArgumentException("Unsupported advice type: " + advice.getClass());
        }

        return interceptors;
    }

    /**
     * CGLIB 的 MethodInterceptor
     * 负责拦截目标对象的方法调用，执行拦截器链或直接调用目标方法
     */
    private static class DynamicAdvisedInterceptor implements MethodInterceptor {

        private final AdvisedSupport advised;
        private final Cglib2AopProxy proxyInstance;

        public DynamicAdvisedInterceptor(AdvisedSupport advised, Cglib2AopProxy proxyInstance) {
            this.advised = advised;
            this.proxyInstance = proxyInstance;
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            // 1. Object 类方法直接调用，不做增强
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(advised.getTargetSource().getTarget(), args);
            }

            // 2. 获取匹配的 Advisor
            List<PointcutAdvisor> eligibleAdvisors = new ArrayList<>();
            for (PointcutAdvisor advisor : advised.getAdvisors()) {
                if (advisor.getPointcut().getMethodMatcher()
                        .matches(method, advised.getTargetSource().getTarget().getClass())) {
                    eligibleAdvisors.add(advisor);
                }
            }

            // 3. 对 Advisors 按 Order 排序
            eligibleAdvisors.sort(Comparator.comparingInt(advisor -> ((Ordered) advisor).getOrder()));


            // 4. 转换 Advisors → MethodInterceptors（通过外部 proxyInstance 调用）
            List<org.aopalliance.intercept.MethodInterceptor> interceptorChain = new ArrayList<>();
            for (Advisor advisor : eligibleAdvisors) {
                interceptorChain.addAll(proxyInstance.adaptAdviceToInterceptor(advisor.getAdvice()));
            }

            // 5. 执行拦截器链，传入自定义 CglibMethodInvocation
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
     * 并重写 proceed() 用 CGLIB 的 methodProxy 调用目标方法
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
                return methodProxy.invoke(target, arguments);
            }
            currentInterceptorIndex++;
            org.aopalliance.intercept.MethodInterceptor interceptor = methodInterceptorList.get(currentInterceptorIndex);
            return interceptor.invoke(this);
        }
    }
}
