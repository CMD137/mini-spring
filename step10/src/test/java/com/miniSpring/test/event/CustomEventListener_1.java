package com.miniSpring.test.event;



import com.miniSpring.context.ApplicationListener;

import java.util.Date;

public class CustomEventListener_1 implements ApplicationListener<CustomEvent> {

    @Override
    public void onApplicationEvent(CustomEvent event) {
        System.out.println("CustomEventListener_1监听到事件：" + event);
        System.out.println("\t收到：" + event.getSource() + "消息;时间：" + new Date());
        System.out.println("\t消息：" + event.getId() + ":" + event.getMessage());
    }

}
