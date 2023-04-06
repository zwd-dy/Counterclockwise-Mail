package com.shadougao.email.listener;

import com.shadougao.email.annotation.RedisChannelListener;
import com.shadougao.email.annotation.RedisResultCode;
import com.shadougao.email.common.utils.GetBeanUtil;
import com.shadougao.email.common.utils.RedisUtil;
import com.shadougao.email.config.RedisConfig;
import com.shadougao.email.entity.*;
import com.shadougao.email.execute.MailExecutor;
import com.shadougao.email.execute.MailParseExecute;
import com.shadougao.email.service.SysEmailPlatformService;
import com.shadougao.email.service.UserBindEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 主程序监听
 */
@RedisChannelListener
@Slf4j
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

    private RedisUtil redisUtil;
    private RedisConfig redisConfig;
    private UserBindEmailService bindEmailService;
    private SysEmailPlatformService platformService;
    private MailExecutor mailExecutor;

    public RedisMainListener() {
        redisUtil = GetBeanUtil.getApplicationContext().getBean(RedisUtil.class);
        redisConfig = GetBeanUtil.getApplicationContext().getBean(RedisConfig.class);
        bindEmailService = GetBeanUtil.getApplicationContext().getBean(UserBindEmailService.class);
        platformService = GetBeanUtil.getApplicationContext().getBean(SysEmailPlatformService.class);
        mailExecutor = GetBeanUtil.getApplicationContext().getBean(MailExecutor.class);
    }

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
        redisUtil.hset(KEY_NODE_TASK, name, new ArrayList<UserBindEmail>());
        // 计算并分配任务
        assignTask();
        this.send(new RedisResult(RedisResultEnum.CONNECT_SUCCESS, name));

    }

    /**
     * 有新邮件
     *
     * @param result
     */
    @RedisResultCode(RedisResultEnum.NEW_MAIL_UIDS)
    public void newMail(RedisResult result) {
        Map<String, Object> map = (Map<String, Object>) result.getData();
        UserBindEmail bindEmail = (UserBindEmail) map.get("bindEmail");
        List<String> newUids = (List<String>) map.get("newUids");
        SysEmailPlatform platform = platformService.getOneById(bindEmail.getPlatformId());

        for (String uid : newUids) {
            // 初始化Mail对象
//            Mail mail = new Mail();
//            mail.setUserId(bindEmail.getUserId());
//            mail.setUid(uid);
//            mail.setBindId(bindEmail.getId());
            // 初始化线程
            MailParseExecute execute = new MailParseExecute();
            execute.setBindEmail(bindEmail);
            execute.setPlatform(platform);
            execute.setUid(Long.parseLong(uid));
            // 执行
            mailExecutor.executeParse(execute);
        }

    }

    /**
     * 重新计算分配任务
     */
    public boolean assignTask() {
        long nodeNum = redisUtil.sGetSetSize(KEY_NODE_LIST);
        // 获取所有需要监控的邮箱账号
        List<UserBindEmail> bindEmails = bindEmailService.getAll();
        // 计算每个节点最多任务量：总任务量 / 节点数 (四舍五入)
        if (bindEmails.size() < nodeNum) {
            return false;
        }
        Long nodeTaskNum = Math.round((double) bindEmails.size() / nodeNum);
        // 获取节点列表
        Set<Object> nodes = redisUtil.sGet(KEY_NODE_LIST);
        // 分配任务
        Iterator<Object> iterator = nodes.iterator();
        List<UserBindEmail> taskList = new ArrayList<>();
        String nodeName = null;

        for (int i = 1; i <= bindEmails.size(); i++) {
            taskList.add(bindEmails.get(i - 1));
            if (i % nodeTaskNum == 0 && iterator.hasNext()) {
                nodeName = (String) iterator.next();
                // 往redis缓存存储任务分配
                redisUtil.hset(KEY_NODE_TASK, nodeName, taskList);
                // 往下一个节点继续分配
                taskList.clear();
            }
        }
        if (iterator.hasNext()) {
            // 剩余的（不够分配量）分配给最后一个节点
            redisUtil.hset(KEY_NODE_TASK, (String) iterator.next(), taskList);
            taskList.clear();
        }
        return true;
    }

    /**
     * 发送添加任务
     *
     * @param bindEmail
     */
    public void addTask(UserBindEmail bindEmail) {
        long nodeNum = redisUtil.sGetSetSize(KEY_NODE_LIST);
        // 获取所有需要监控的邮箱账号
        List<UserBindEmail> bindEmails = bindEmailService.getAll();

        if (nodeNum == 0) {
            return;
        }
        // 计算每个节点最多任务量：总任务量 / 节点数 (四舍五入)
        Long nodeTaskNum = Math.round((double) bindEmails.size() / nodeNum);
        if (bindEmails.size() < nodeNum) {
            nodeTaskNum = (long) bindEmails.size();
        }
        log.info("[cluster] - 添加任务邮箱：{}，节点数：{}，平均任务量：{}", bindEmail.getEmailUser(), nodeNum, nodeTaskNum);
        // 获取节点列表
        Set<Object> nodes = redisUtil.hkeys(KEY_NODE_TASK);
        for (Object node : nodes) {
            String nodeName = null;
            if (node != null) {
                nodeName = (String) node;
                List<UserBindEmail> bindUserList = (List<UserBindEmail>) redisUtil.hget(KEY_NODE_TASK, nodeName);
                if (bindUserList != null && bindUserList.size() < nodeTaskNum) {
                    bindUserList.add(bindEmail);
                    redisUtil.hset(KEY_NODE_TASK, nodeName, bindUserList);
                    send(new RedisResult(RedisResultEnum.TASK_NEW_ADD, bindEmail, nodeName));
                    break;
                }
            }
        }
    }

    /**
     * 发送删除任务
     *
     * @param bindId
     */
    public void delTask(String bindId) {
        // 获取节点列表
        Set<Object> nodes = redisUtil.hkeys(KEY_NODE_TASK);
        for (Object node : nodes) {
            String nodeName = null;
            if (node != null) {
                nodeName = (String) node;
                List<UserBindEmail> bindUserList = (List<UserBindEmail>) redisUtil.hget(KEY_NODE_TASK, nodeName);
                if (bindUserList != null) {
                    List<UserBindEmail> newList = bindUserList.stream().filter(item -> !item.getId().equals(bindId)).collect(Collectors.toList());
                    redisUtil.hset(KEY_NODE_TASK, nodeName, newList);
                    send(new RedisResult(RedisResultEnum.TASK_DEL, bindId, nodeName));
                }
            }
        }
    }

    /**
     * 发送任务更新
     */
    public void taskUpdate(String bindId) {
        send(new RedisResult(RedisResultEnum.TASK_UPDATE, bindId));
    }


}
