package com.shadougao.email.listener;

import com.shadougao.email.annotation.RedisChannelListener;
import com.shadougao.email.annotation.RedisResultCode;
import com.shadougao.email.common.utils.RedisUtil;
import com.shadougao.email.config.RedisConfig;
import com.shadougao.email.entity.RedisResult;
import com.shadougao.email.entity.RedisResultEnum;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.service.UserBindEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 主程序监听
 */
@RedisChannelListener
@Component
public class RedisMainListener {

    // 节点名字前缀
    public static final String NODE_PREFIX = "q";
    // key前缀
    public static final String KEY_PREFIX = "email:task:";
    public static final String KEY_NODE_LIST = KEY_PREFIX + "node_list";
    public static final String KEY_NODE_TASK = KEY_PREFIX + "node_task";

    /*
        发：mainChannel
        收：executeChannel
 */

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisConfig redisConfig;
    @Autowired
    private UserBindEmailService bindEmailService;

    public void send(RedisResult result) {
        redisUtil.publist(redisConfig.mainChannel, result);
    }

    /**
     * 有新节点加入
     *
     * @param result
     */
    @RedisResultCode(RedisResultEnum.CONNECT_MAIN)
    public void newExecute(RedisResult result) {
        // 添加子节点数，为子节点设定名字
        long nodeNum = redisUtil.sGetSetSize(KEY_NODE_LIST) + 1;
        String name = NODE_PREFIX + nodeNum;
        redisUtil.sSet(KEY_NODE_LIST, name);
        // 计算并分配任务
        assignTask();
        this.send(new RedisResult(RedisResultEnum.CONNECT_SUCCESS, name));
    }




    /**
     * 重新计算分配任务
     */
    public void assignTask(){
        long nodeNum = redisUtil.sGetSetSize(KEY_NODE_LIST);
        // 获取所有需要监控的邮箱账号
        List<UserBindEmail> bindEmails = bindEmailService.getAll();
        // 计算每个节点最多任务量：总任务量 / 节点数 (四舍五入)
        Long nodeTaskNum = Math.round((double) bindEmails.size() / nodeNum);
        // 获取节点列表
        Set<Object> nodes = redisUtil.sGet(KEY_NODE_LIST);
        // 分配任务
        Iterator<Object> iterator = nodes.iterator();
        List<UserBindEmail> taskList = new ArrayList<>();
        String nodeName = null;

        for (int i = 1; i <= bindEmails.size(); i++) {
            taskList.add(bindEmails.get(i-1));
            if(i % nodeTaskNum == 0 && iterator.hasNext()){
                nodeName = (String) iterator.next();
                // 往redis缓存存储任务分配
                redisUtil.hset(KEY_NODE_TASK, nodeName, taskList);
                // 往下一个节点继续分配
                taskList.clear();
            }
        }
        if(iterator.hasNext()){
            // 剩余的（不够分配量）分配给最后一个节点
            redisUtil.hset(KEY_NODE_TASK, (String) iterator.next(), taskList);
            taskList.clear();
        }
    }


}
