package com.miniSpring.beans.factory.xml;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.PropertyValue;
import com.miniSpring.beans.factory.config.BeanDefinition;
import com.miniSpring.beans.factory.config.BeanReference;
import com.miniSpring.beans.factory.support.AbstractBeanDefinitionReader;
import com.miniSpring.beans.factory.support.BeanDefinitionRegistry;
import com.miniSpring.context.annotation.ClassPathBeanDefinitionScanner;
import com.miniSpring.core.io.Resource;
import com.miniSpring.core.io.ResourceLoader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * XmlBeanDefinitionReader 继承自 AbstractBeanDefinitionReader，
 * 负责从 XML 资源中读取并解析 Bean 定义信息，
 * 然后注册到 BeanDefinitionRegistry 中。
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    /**
     * 构造函数，只传入 BeanDefinition 注册表，使用默认资源加载器
     * @param registry BeanDefinition 注册表
     */
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    /**
     * 构造函数，传入 BeanDefinition 注册表和资源加载器
     * @param registry BeanDefinition 注册表
     * @param resourceLoader 资源加载器
     */
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }

    /**
     * 从单个 Resource 资源中加载 Bean 定义
     * @param resource 资源，如 classpath 下的 XML 文件
     * @throws BeansException 加载或解析异常
     */
    @Override
    public void loadBeanDefinitions(Resource resource) throws BeansException {
        try {
            // 通过 try-with-resources 自动关闭输入流
            try (InputStream inputStream = resource.getInputStream()) {
                // 委托给具体的加载方法进行 XML 解析
                doLoadBeanDefinitions(inputStream);
            }
        } catch (IOException | ClassNotFoundException | DocumentException e) {
            // 包装异常，抛出自定义 BeansException
            throw new BeansException("IOException parsing XML document from " + resource, e);
        }
    }

    /**
     * 批量从多个 Resource 资源中加载 Bean 定义
     * @param resources 多个资源数组（可变参数）
     * @throws BeansException 加载异常
     */
    @Override
    public void loadBeanDefinitions(Resource... resources) throws BeansException {
        for (Resource resource : resources) {
            // 循环调用单个资源加载方法
            loadBeanDefinitions(resource);
        }
    }

    /**
     * 通过资源路径字符串加载 Bean 定义
     * @param location 资源路径，如 "classpath:beans.xml"
     * @throws BeansException 加载异常
     */
    @Override
    public void loadBeanDefinitions(String location) throws BeansException {
        ResourceLoader resourceLoader = getResourceLoader();
        // 使用资源加载器将路径转成 Resource 对象
        Resource resource = resourceLoader.getResource(location);
        // 调用 Resource 版本的加载方法
        loadBeanDefinitions(resource);
    }

    /**
     * 批量从多个资源路径字符串中加载 Bean 定义
     * @param locations 多个资源路径字符串数组
     * @throws BeansException 加载异常
     */
    @Override
    public void loadBeanDefinitions(String... locations) throws BeansException {
        for (String location : locations) {
            loadBeanDefinitions(location);
        }
    }

    /**
     * 解析 XML 输入流，读取 Bean 定义信息并注册
     * @param inputStream XML 配置文件输入流
     * @throws ClassNotFoundException 找不到指定类异常
     */
    protected void doLoadBeanDefinitions(InputStream inputStream) throws ClassNotFoundException, DocumentException {
        // 1. 创建 SAXReader 对象，使用 dom4j 解析 XML
        SAXReader reader = new SAXReader();
        // 2. 读取输入流，生成 Document 对象（DOM 树）
        Document document = reader.read(inputStream);
        // 3. 获取根元素 <beans>
        Element root = document.getRootElement();

        // ---------------------------
        // 4. 解析 <component-scan> 标签
        //    目的是扫描指定包下的类并生成 BeanDefinition
        // ---------------------------
        Element componentScan = root.element("component-scan");
        if (componentScan != null) {
            // 5. 获取 base-package 属性值，支持逗号分隔多个包
            String scanPath = componentScan.attributeValue("base-package");
            // 6. 如果 base-package 为空，抛出异常
            if (StrUtil.isEmpty(scanPath)) {
                throw new BeansException("The value of base-package attribute can not be empty or null");
            }
            // 7. 调用 scanPackage 方法扫描包
            scanPackage(scanPath);
        }

        // ---------------------------
        // 8. 解析 <bean> 标签
        // ---------------------------
        List<Element> beanList = root.elements("bean");
        for (Element bean : beanList) {
            // 9. 获取 bean 标签的基本属性：id、name、class、init-method、destroy-method、scope
            String id = bean.attributeValue("id");
            String name = bean.attributeValue("name");
            String className = bean.attributeValue("class");
            String initMethod = bean.attributeValue("init-method");
            String destroyMethodName = bean.attributeValue("destroy-method");
            String beanScope = bean.attributeValue("scope");

            // 10. 根据 class 属性获取 Class 对象
            Class<?> clazz = Class.forName(className);
            // 11. 确定 Bean 名称，优先使用 id，其次 name，最后类名首字母小写
            String beanName = StrUtil.isNotEmpty(id) ? id : name;
            if (StrUtil.isEmpty(beanName)) {
                beanName = StrUtil.lowerFirst(clazz.getSimpleName());
            }

            // 12. 创建 BeanDefinition 对象，用于封装 bean 信息
            BeanDefinition beanDefinition = new BeanDefinition(clazz);
            beanDefinition.setInitMethodName(initMethod);
            beanDefinition.setDestroyMethodName(destroyMethodName);
            // 13. 如果 scope 不为空，设置到 BeanDefinition
            if (StrUtil.isNotEmpty(beanScope)) {
                beanDefinition.setScope(beanScope);
            }

            // ---------------------------
            // 14. 解析 <property> 子标签
            //    用于读取 Bean 属性并封装成 PropertyValue 对象
            // ---------------------------
            List<Element> propertyList = bean.elements("property");
            for (Element property : propertyList) {
                // 15. 获取属性名、属性值、引用
                String attrName = property.attributeValue("name");
                String attrValue = property.attributeValue("value");
                String attrRef = property.attributeValue("ref");
                // 16. 判断是引用其他 Bean 还是普通值
                Object value = StrUtil.isNotEmpty(attrRef) ? new BeanReference(attrRef) : attrValue;
                // 17. 封装属性名和值到 PropertyValue
                PropertyValue propertyValue = new PropertyValue(attrName, value);
                // 18. 添加到 BeanDefinition 的属性集合中
                beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
            }

            // 19. 检查容器中是否已有同名 Bean，防止重复注册
            if (getRegistry().containsBeanDefinition(beanName)) {
                throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
            }

            // 20. 注册 BeanDefinition 到注册表中
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }
    }

    /**
     * 扫描指定的包路径，生成 BeanDefinition 并注册到容器
     * @param scanPath 逗号分隔的基础包路径
     */
    private void scanPackage(String scanPath) {
        // 1. 分割多个包名
        String[] basePackages = StrUtil.splitToArray(scanPath, ',');
        // 2. 创建扫描器，传入 BeanDefinition 注册器
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(getRegistry());
        // 3. 执行扫描并注册 BeanDefinition
        scanner.doScan(basePackages);
    }


}


