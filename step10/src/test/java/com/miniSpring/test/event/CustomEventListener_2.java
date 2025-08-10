package com.miniSpring.test.event;



import com.miniSpring.context.ApplicationListener;

import java.util.Date;

public class CustomEventListener_2 implements ApplicationListener<CustomEvent> {

    @Override
    public void onApplicationEvent(CustomEvent event) {
        System.out.println("CustomEventListener_2监听到事件：" + event);
        System.out.println("\t处理Custom业务中......");
    }

}
