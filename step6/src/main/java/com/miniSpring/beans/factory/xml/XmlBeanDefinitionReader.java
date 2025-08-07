package com.miniSpring.beans.factory.xml;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.miniSpring.beans.BeansException;
import com.miniSpring.beans.PropertyValue;
import com.miniSpring.beans.factory.config.BeanDefinition;
import com.miniSpring.beans.factory.config.BeanReference;
import com.miniSpring.beans.factory.support.AbstractBeanDefinitionReader;
import com.miniSpring.beans.factory.support.BeanDefinitionRegistry;
import com.miniSpring.core.io.Resource;
import com.miniSpring.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;

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
        } catch (IOException | ClassNotFoundException e) {
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
    protected void doLoadBeanDefinitions(InputStream inputStream) throws ClassNotFoundException {
        // 解析 XML 文档
        Document doc = XmlUtil.readXML(inputStream);
        Element root = doc.getDocumentElement();
        NodeList childNodes = root.getChildNodes();

        // 遍历根节点的所有子节点
        for (int i = 0; i < childNodes.getLength(); i++) {
            // 过滤非 Element 类型节点
            if (!(childNodes.item(i) instanceof Element)) continue;
            // 只处理 <bean> 标签
            if (!"bean".equals(childNodes.item(i).getNodeName())) continue;

            // 强转成 Element 方便操作
            Element bean = (Element) childNodes.item(i);
            // 获取 bean 的 id、name、class 属性
            String id = bean.getAttribute("id");
            String name = bean.getAttribute("name");
            String className = bean.getAttribute("class");

            // 加载 bean 对应的 Class 对象
            Class<?> clazz = Class.forName(className);

            // 确定 bean 名称，优先使用 id，再使用 name，最后默认使用类名首字母小写
            String beanName = StrUtil.isNotEmpty(id) ? id : name;
            if (StrUtil.isEmpty(beanName)) {
                beanName = StrUtil.lowerFirst(clazz.getSimpleName());
            }

            // 创建 BeanDefinition，保存 Class 信息
            BeanDefinition beanDefinition = new BeanDefinition(clazz);

            // 解析 <property> 子标签，填充 Bean 属性信息
            for (int j = 0; j < bean.getChildNodes().getLength(); j++) {
                if (!(bean.getChildNodes().item(j) instanceof Element)) continue;
                if (!"property".equals(bean.getChildNodes().item(j).getNodeName())) continue;

                Element property = (Element) bean.getChildNodes().item(j);
                String attrName = property.getAttribute("name");
                String attrValue = property.getAttribute("value");
                String attrRef = property.getAttribute("ref");

                // 判断属性值是普通值还是引用其他 Bean
                Object value = StrUtil.isNotEmpty(attrRef) ? new BeanReference(attrRef) : attrValue;

                // 创建 PropertyValue 对象，封装属性名和值
                PropertyValue propertyValue = new PropertyValue(attrName, value);
                // 添加到 BeanDefinition 中的属性集合
                beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
            }

            // 判断是否有重复 BeanName，防止覆盖
            if (getRegistry().containsBeanDefinition(beanName)) {
                throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
            }

            // 注册 BeanDefinition 到注册表中
            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }
    }

}


