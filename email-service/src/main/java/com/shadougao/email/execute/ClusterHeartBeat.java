package com.shadougao.email.execute;

import com.shadougao.email.common.utils.GetBeanUtil;
import com.shadougao.email.common.utils.RedisUtil;
import com.shadougao.email.config.RedisConfig;
import com.shadougao.email.entity.RedisResult;
import com.shadougao.email.entity.RedisResultEnum;
import com.shadougao.email.listener.RedisMainListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 执行器心跳包
 */
@Component
@Slf4j
public class ClusterHeartBeat implements Runnable {

    public static final String KEY_PREFIX = "email:task:";
    public static final String KEY_NODE_LIST = KEY_PREFIX + "node_list";
    public static final String KEY_NODE_TASK = KEY_PREFIX + "node_task";
    public static final String KEY_NODE_LAST_HEART = KEY_PREFIX + "node_last_heart";
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisConfig redisConfig;
    @Value("${email.cluster.heart-beat-cycle}")
    private int cycle;  // 心跳包周期
    @Value("${email.cluster.cluster-timeout}")
    private long timeout;

    @Override
    public void run() {
        while (true) {
            Long time = System.currentTimeMillis() / 1000;
            redisUtil.publist(redisConfig.mainChannel, new RedisResult(RedisResultEnum.HEART_BEAT, "ok?"));
            // 获取所有节点
            Set<Object> nodes = redisUtil.sGet(KEY_NODE_LIST);
            for (Object node : nodes) {
                String nodeName = (String) node;
                // 获取子节点最后一次心跳时间
                Object objLastTime = redisUtil.hget(KEY_NODE_LAST_HEART, nodeName);
                long lastTime = 0l;
                if (objLastTime != null) {
                    lastTime = (long) objLastTime;
                }

                // 如果子节点超过 [timeout] 没有更新时间，说明寄了，需要给所有节点重新分配任务
                if (time - lastTime > timeout) {   // 秒单位对比
                    // 剔除该子节点信息
                    redisUtil.setRemove(KEY_NODE_LIST, nodeName);
                    redisUtil.hdel(KEY_NODE_LAST_HEART, nodeName);
                    redisUtil.hdel(KEY_NODE_TASK, nodeName);
                    // 重新分配任务
                    if (new RedisMainListener().assignTask()) {
                        // 告知所有子节点重新获取任务
                        redisUtil.publist(redisConfig.mainChannel, new RedisResult(RedisResultEnum.TASK_RETRIEVE, "pull task"));
                    }
                    log.error("节点：{} 已掉线，清理节点在缓存中的相关信息", nodeName);
                }
            }

            try {
                Thread.sleep(cycle);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
