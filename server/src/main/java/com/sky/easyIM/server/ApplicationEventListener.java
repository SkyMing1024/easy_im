package com.sky.easyIM.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;


/**
 * 监听器，监听服务启动事件ApplicationStartedEvent
 */
@Slf4j
@Service
public class ApplicationEventListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        log.info("springboot 服务启动");
        WebSocketServer sever = new WebSocketServer("/chat");

        sever.start((short) 8081);
    }
}
