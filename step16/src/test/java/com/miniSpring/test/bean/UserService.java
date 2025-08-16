package com.miniSpring.test.bean;

import com.miniSpring.beans.factory.annotation.Autowired;
import com.miniSpring.beans.factory.annotation.Value;
import com.miniSpring.context.annotation.Component;

import java.util.Random;

@Component("userService")
public class UserService implements IUserService {

    @Value("${token}")
    private String token;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TempService tempService;

    public String queryUserInfo() {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return userDao.queryUserName("10001") + "，" + token;
    }

    public void useTempService() {
        tempService.talk();
    }

    @Override
    public String toString() {
        return "UserService#token = { " + token + " }";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

