import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultBeanFactory implements BeanFactory{

    private ConcurrentMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    @Override
    public Object getBean(String name) {
        Object bean = beanDefinitionMap.get(name).getBean();
        if (bean == null){
            throw new RuntimeException("bean not found");
        }
        return bean;
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        if (beanDefinitionMap.containsKey(name)){
            throw new RuntimeException("bean already exists");
        }
        beanDefinitionMap.put(name,beanDefinition);
    }
}
