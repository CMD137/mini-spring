package com.miniSpring.test.bean;

import java.util.Random;

public class UserService implements IUserService{
    private String uId;
    private String name;

    @Override
    public String queryUserName() {
        try {
            // 模拟方法执行耗时：随机0~99毫秒
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("UserService.queryUserName 原方法执行:\tCMD137---------10086");
        return "CMD137,10086";
    }

}
