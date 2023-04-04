package com.shadougao.email.service.impl;

import com.alibaba.fastjson.JSON;
import com.mchange.lang.LongUtils;
import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.utils.GetBeanUtil;
import com.shadougao.email.common.utils.RedisUtil;
import com.shadougao.email.config.RedisConfig;
import com.shadougao.email.entity.RedisResult;
import com.shadougao.email.entity.RedisResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
@ServerEndpoint("/ws/{userId}")
public class WebSocket {

    /**
     * 线程安全的无序的集合
     */
    private static final CopyOnWriteArraySet<Session> SESSIONS = new CopyOnWriteArraySet<>();

    /**
     * 存储在线连接数
     */
    private static final Map<String, Session> SESSION_POOL = new HashMap<>();
    private static final Map<Session, String> SESSION_USER_POOL = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        try {
            SESSIONS.add(session);
            SESSION_POOL.put(userId, session);
            SESSION_USER_POOL.put(session, userId);
            log.info("【WebSocket】有新的连接，总数为：{}", SESSIONS.size());
            // 告知所有子节点该用户在线，修改轮询周期
            if (!"undefined".equals(userId)) {
                RedisUtil redisUtil = GetBeanUtil.getApplicationContext().getBean(RedisUtil.class);
                RedisConfig redisConfig = GetBeanUtil.getApplicationContext().getBean(RedisConfig.class);
                redisUtil.publist(redisConfig.mainChannel, new RedisResult(RedisResultEnum.USER_ONLINE, Long.parseLong(userId)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        try {
            String userId = SESSION_USER_POOL.get(session);
            SESSIONS.remove(session);
            SESSION_USER_POOL.remove(session);
            log.info("【WebSocket】连接断开，总数为：{}", SESSIONS.size());
            // 告知所有子节点该用户离线，修改轮询周期
            if (!"undefined".equals(userId)) {
                RedisUtil redisUtil = GetBeanUtil.getApplicationContext().getBean(RedisUtil.class);
                RedisConfig redisConfig = GetBeanUtil.getApplicationContext().getBean(RedisConfig.class);
                redisUtil.publist(redisConfig.mainChannel, new RedisResult(RedisResultEnum.USER_OFFLINE, Long.parseLong(userId)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("【WebSocket】收到客户端消息：{}", message);
    }

    /**
     * 此为广播消息
     *
     * @param message 消息
     */
    public void sendAllMessage(String message) {
        log.info("【WebSocket消息】广播消息：" + message);
        for (Session session : SESSIONS) {
            try {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 此为单点消息
     *
     * @param userId  用户编号
     * @param message 消息
     */
    public static void sendOneMessage(String userId, String message) {
        Session session = SESSION_POOL.get(userId);
        if (session != null && session.isOpen()) {
            try {
                synchronized (session) {
                    log.info("【WebSocket消息】单点消息：" + message);
                    session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 此为单点消息(多人)
     *
     * @param userIds 用户编号列表
     * @param message 消息
     */
    public void sendMoreMessage(String[] userIds, String message) {
        for (String userId : userIds) {
            Session session = SESSION_POOL.get(userId);
            if (session != null && session.isOpen()) {
                try {
                    log.info("【WebSocket】单点消息：{}", message);
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
