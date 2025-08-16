package com.miniSpring.test.bean;

import com.miniSpring.beans.factory.annotation.Autowired;
import com.miniSpring.context.annotation.Component;

@Component
public class TempService {

    @Autowired
    private IUserService userService;

    public void queryUserInfo() {

        String result = userService.queryUserInfo();
        System.out.println("TempService 调用了 userService.queryUserInfo()："+result);
    }

    public void talk() {
        System.out.println("TempService:talk! ");
    }
}
