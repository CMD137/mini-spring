package com.miniSpring.test.bean;

import java.util.HashMap;
import java.util.Map;

public class UserDao {

    private static Map<String, String> hashMap = new HashMap<>();

    public void initDataMethod(){
        System.out.println("执行：Dao的 init-method");
        hashMap.put("10001", "CMD137");
        hashMap.put("10002", "CMD138");
        hashMap.put("10003", "CMD139");
    }

    public void destroyDataMethod(){
        System.out.println("执行：Dao的 destroy-method");
        hashMap.clear();
    }

    public String queryUserName(String uId) {
        return hashMap.get(uId);
    }

}