package com.miniSpring.util;

/**
 * 字符串值解析器接口。
 * 定义了解析字符串的方法，通常用于解析占位符或表达式，
 * 将输入字符串转换为解析后的实际值。
 */
public interface StringValueResolver {
    String resolveStringValue(String strVal);
}
