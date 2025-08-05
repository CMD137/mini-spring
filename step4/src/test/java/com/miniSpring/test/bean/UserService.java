package com.miniSpring.test.bean;

public class UserService {

    private String name;

    public UserService() {
        // 必须有这个无参构造器，CGLIB 需要它
    }

    public UserService(String name) {
        this.name = name;
    }

    private String uId;

    private UserDao userDao;

    public void queryUserInfo() {
        System.out.println("查询用户信息：" + userDao.queryUserName(uId));
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("");
        sb.append("").append(name);
        return sb.toString();
    }
}
