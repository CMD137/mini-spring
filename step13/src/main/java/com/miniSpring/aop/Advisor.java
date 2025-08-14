package com.miniSpring.aop;

import org.aopalliance.aop.Advice;

//表示一个“增强器”，封装了具体的增强逻辑（Advice）
public interface Advisor {

    Advice getAdvice();

}
