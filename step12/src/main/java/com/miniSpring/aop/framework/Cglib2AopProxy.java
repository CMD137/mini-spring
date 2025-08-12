package com.miniSpring.aop.framework;

import com.miniSpring.aop.AdvisedSupport;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
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
        enhancer.setInterfaces(advised.getTargetSource().getTargetClass());
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
            // 判断方法是否匹配切点
            if (advised.getMethodMatcher() == null ||
                    advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
                // 执行拦截器链，传入自定义的 CglibMethodInvocation
                CglibMethodInvocation invocation = new CglibMethodInvocation(
                        advised.getTargetSource().getTarget(),
                        method,
                        args,
                        methodProxy,
                        advised.getMethodInterceptorList()
                );
                return invocation.proceed();
            } else {
                // 不匹配则直接调用目标方法
                return methodProxy.invoke(advised.getTargetSource().getTarget(), args);
            }
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
