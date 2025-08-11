package com.miniSpring.aop.aspectj;

import com.miniSpring.aop.ClassFilter;
import com.miniSpring.aop.MethodMatcher;
import com.miniSpring.aop.Pointcut;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * AspectJExpressionPointcut 是一个基于 AspectJ 表达式的切点实现类，
 * 同时实现了 Pointcut、ClassFilter、MethodMatcher 接口。
 *
 * 它的职责是：
 *  - 解析 AspectJ 表达式
 *  - 判断给定的类或方法是否符合切点规则
 *  - 提供 ClassFilter 和 MethodMatcher 实例给 AOP 框架使用
 *
 * 在 AOP 中，Pointcut 用来定位切入点，Advice 用来定义切面逻辑，
 * 而这个类就是负责“用表达式匹配目标方法”的那部分。
 */
public class AspectJExpressionPointcut implements Pointcut, ClassFilter, MethodMatcher {

    /**
     * 定义当前切点支持的 Pointcut 类型（原语）。
     * 这里只支持 "execution" 类型的切点表达式，
     * 即基于方法执行的切入点，例如：
     * execution(* com.example.service.UserService.*(..))
     */
    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

    // 静态代码块：初始化支持的切点类型集合
    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
    }

    /**
     * AspectJ 解析后的切点表达式对象。
     * 由 AspectJ 的 PointcutParser 生成，用来执行实际的匹配判断。
     */
    private final PointcutExpression pointcutExpression;

    /**
     * 构造方法：传入 AspectJ 表达式并进行解析
     *
     * @param expression 切点表达式（如 execution(* com.example..*(..))）
     */
    public AspectJExpressionPointcut(String expression) {
        // 创建一个 PointcutParser，指定支持的切点类型和类加载器
        PointcutParser pointcutParser =
                PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
                        SUPPORTED_PRIMITIVES, this.getClass().getClassLoader()
                );

        // 解析传入的切点表达式
        this.pointcutExpression = pointcutParser.parsePointcutExpression(expression);
    }

    /**
     * 判断某个类是否可能匹配当前切点表达式
     *
     * @param clazz 目标类
     * @return true 表示该类中可能存在匹配的方法
     */
    @Override
    public boolean matches(Class<?> clazz) {
        return pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    /**
     * 判断某个方法是否符合切点表达式
     *
     * @param method      目标方法
     * @param targetClass 方法所在的目标类
     * @return true 表示该方法符合切点规则
     */
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        // matchesMethodExecution() 用于方法执行匹配
        // alwaysMatches() 表示是否肯定匹配
        return pointcutExpression.matchesMethodExecution(method).alwaysMatches();
    }

    /**
     * 获取类过滤器（ClassFilter）
     * 由于本类本身实现了 ClassFilter，所以直接返回 this
     */
    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    /**
     * 获取方法匹配器（MethodMatcher）
     * 由于本类本身实现了 MethodMatcher，所以直接返回 this
     */
    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }


}

