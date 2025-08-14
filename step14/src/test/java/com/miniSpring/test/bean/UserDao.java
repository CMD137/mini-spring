package com.miniSpring.test.bean;

import com.miniSpring.context.annotation.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDao {

    private static Map<String, String> hashMap = new HashMap<>();

    static {
        hashMap.put("10001", "CMD137，北京，亦庄");
        hashMap.put("10002", "CMD138，上海，尖沙咀");
        hashMap.put("10003", "CMD139，天津，东丽区");
    }

    public String queryUserName(String uId) {
        return hashMap.get(uId);
    }

}
