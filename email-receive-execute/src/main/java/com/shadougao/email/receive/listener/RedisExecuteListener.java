package com.shadougao.email.receive.listener;

import com.shadougao.email.annotation.RedisChannelListener;
import com.shadougao.email.annotation.RedisResultCode;
import com.shadougao.email.dao.mongo.SysEmailPlatformDao;
import com.shadougao.email.dao.mongo.UserBindEmailDao;
import com.shadougao.email.entity.RedisResult;
import com.shadougao.email.entity.RedisResultEnum;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.receive.config.GlobalConfig;
import com.shadougao.email.receive.config.RedisConfig;
import com.shadougao.email.receive.execute.MailListener;
import com.shadougao.email.receive.execute.MailTask;
import com.shadougao.email.receive.utils.GetBeanUtil;
import com.shadougao.email.receive.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;


import java.util.List;


/**
 * 执行器监听
 */
//@Component
@RedisChannelListener
@Slf4j
public class RedisExecuteListener {

    // key前缀
    public static final String KEY_PREFIX = "email:task:";
    public static final String KEY_NODE_LIST = KEY_PREFIX + "node_list";
    public static final String KEY_NODE_TASK = KEY_PREFIX + "node_task";
    public static final String KEY_NODE_LAST_HEART = KEY_PREFIX + "node_last_heart";

    /*
        发：executeChannel
        收：mainChannel
 */

    private MailTask mailTask;
    private RedisUtil redisUtil;
    private RedisConfig redisConfig;
    private SysEmailPlatformDao platformDao;
    private UserBindEmailDao bindEmailDao;
    private GlobalConfig globalConfig;

    public RedisExecuteListener() {
        mailTask = GetBeanUtil.getApplicationContext().getBean(MailTask.class);
        redisUtil = GetBeanUtil.getApplicationContext().getBean(RedisUtil.class);
        redisConfig = GetBeanUtil.getApplicationContext().getBean(RedisConfig.class);
        platformDao = GetBeanUtil.getApplicationContext().getBean(SysEmailPlatformDao.class);
        bindEmailDao = GetBeanUtil.getApplicationContext().getBean(UserBindEmailDao.class);
        globalConfig = GetBeanUtil.getApplicationContext().getBean(GlobalConfig.class);
    }

    public void send(RedisResult result) {
        redisUtil.publist(redisConfig.executeChannel, result);
    }

    /**
     * 连接主程序成功，开始从缓存获取任务并执行
     *
     * @param
     */
    @RedisResultCode(RedisResultEnum.CONNECT_SUCCESS)
    public void connectSuccess(RedisResult result) {
        if (globalConfig.nodeName == null) {
            // 获取节点名称
            globalConfig.nodeName = (String) result.getData();
        }
        // 分配任务
        taskRetrieve(null);
        log.info("[{}] - 成功连接到主程序");
    }

    /**
     * 重新获取任务
     *
     * @param result
     */
    @RedisResultCode(RedisResultEnum.TASK_RETRIEVE)
    public void taskRetrieve(RedisResult result) {
        log.info("正在关闭所有线程");
        // 关闭所有线程的任务
        int j = 0;
        List<MailListener> threads = mailTask.getThreads();
        for (int i = 0; i - j < threads.size(); i++) {
            MailListener t = threads.get(i - j);
            t.closeTask();
            threads.remove(t);
            j++;
        }
//        System.out.println(Thread.currentThread().getName() + "，当前管理线程数：" + mailTask.getThreads().size());
        // 重新分配任务
        getTask();
        log.info("[{}] - 重新分配任务，当前管理线程数：{}",globalConfig.nodeName,mailTask.getThreads().size());
    }

    /**
     * 新任务加入
     *
     * @param result
     */
    @RedisResultCode(RedisResultEnum.TASK_NEW_ADD)
    public void taskNewAdd(RedisResult result) {
        String nodeName = result.getNodeName();
        if (!nodeName.equals(nodeName)) {
            return;
        }
        UserBindEmail bindEmail = (UserBindEmail) result.getData();
        addTaskListener(bindEmail);
        log.info("[{}] - 加入邮箱账号监听线程，账号：{}，绑定ID：{}",globalConfig.nodeName,bindEmail.getEmailUser(),bindEmail.getId());
    }

    /**
     * 有任务被删除
     */
    @RedisResultCode(RedisResultEnum.TASK_DEL)
    public void taskDel(RedisResult result) {
        Object objId = result.getData();
        if (objId != null) {
            String bindId = (String) objId;
            delTaskListener(bindId);
            log.info("[{}] - 邮箱绑定ID：{} 删除监听线程",globalConfig.nodeName,bindId);
        }
    }

    /**
     * 更新任务
     */
    @RedisResultCode(RedisResultEnum.TASK_UPDATE)
    public void taskUpdate(RedisResult result) {
        Object objId = result.getData();
        if (objId != null) {
            String bindId = (String) objId;
            // 将旧任务删除
            delTaskListener(bindId);
            // 将新任务添加到监听线程中
            UserBindEmail bindEmail = bindEmailDao.getOneById(bindId);
            addTaskListener(bindEmail);
            log.info("[{}] - 更新邮箱信息，重新启动监听线程，邮箱账号：{}，bindId：{}", globalConfig.nodeName, bindEmail.getEmailUser(), bindId);
        }
    }


    /**
     * 主程序服务端心跳包
     *
     * @param result
     */
    @RedisResultCode(RedisResultEnum.HEART_BEAT)
    public void heartBeat(RedisResult result) {
        if (globalConfig.nodeName != null) {
            redisUtil.hset(KEY_NODE_LAST_HEART, globalConfig.nodeName, System.currentTimeMillis() / 1000);
            log.info("[{}] - 确认存活！", globalConfig.nodeName);
        }
    }

    /**
     * 用户上线，修改周期
     */
    @RedisResultCode(RedisResultEnum.USER_ONLINE)
    public void userOnline(RedisResult result) {
        Long userId = (Long) result.getData();
        List<MailListener> threads = mailTask.getThreads();
        for (int i = 0; i < threads.size(); i++) {
            MailListener t = threads.get(i);
            if (t.getBindEmail().getUserId() == userId) {
                t.setSleepTime(globalConfig.userOnlineCycle);
                log.info("[{}] - 用户id：{} 上线，修改线程周期为{}秒", globalConfig.nodeName, userId, globalConfig.userOnlineCycle);
            }
        }
    }

    /**
     * 用户离线，修改周期
     */
    @RedisResultCode(RedisResultEnum.USER_OFFLINE)
    public void userOffline(RedisResult result) {
        Long userId = (Long) result.getData();
        List<MailListener> threads = mailTask.getThreads();
        for (int i = 0; i < threads.size(); i++) {
            MailListener t = threads.get(i);
            if (t.getBindEmail().getUserId() == userId) {
                t.setSleepTime(globalConfig.normalCycle);
                log.info("[{]] - 用户id：{} 离线，修改线程周期为{}秒", globalConfig.nodeName, userId, globalConfig.normalCycle);
            }
        }
    }

    public void getTask() {
        // 获取任务
        // 获取邮箱平台列表
        List<SysEmailPlatform> platforms = platformDao.getAll();
        List<UserBindEmail> bindEmails = (List<UserBindEmail>) redisUtil.hget(KEY_NODE_TASK, globalConfig.nodeName);
        // 清空线程管理
        mailTask.getThreads().clear();
        if (bindEmails != null) {
            for (UserBindEmail bindEmail : bindEmails) {
                MailListener listener = new MailListener();
                // 设置监控邮箱
                listener.setBindEmail(bindEmail);
                // 设置对应邮箱平台
                listener.setPlatform(platforms.stream().filter(p -> bindEmail.getPlatformId().equals(p.getId())).findFirst().get());
                // 设置轮询周期（秒级）
                listener.setSleepTime(globalConfig.normalCycle);
                mailTask.getThreads().add(listener);
                // 执行
                mailTask.getPoolExecutor().execute(listener);
            }
        }
    }

    /**
     * 【通用方法】将邮箱加入监听
     *
     * @param bindEmail
     */
    public void addTaskListener(UserBindEmail bindEmail) {
        // 获取该任务的平台
        SysEmailPlatform platform = platformDao.getOneById(bindEmail.getPlatformId());
        if (platform == null) {
            // TODO 平台不存在，无法监听
            log.error("邮箱平台不存在！\n email：{}，platform：{}", bindEmail.getEmailUser(), platform);
            return;
        }
        // 将任务加入监听线程
        MailListener listener = new MailListener();
        // 设置监控邮箱
        listener.setBindEmail(bindEmail);
        // 设置对应邮箱平台
        listener.setPlatform(platform);
        // 设置轮询周期（秒级）
        listener.setSleepTime(globalConfig.normalCycle);
        // 执行
        mailTask.getPoolExecutor().execute(listener);
    }

    /**
     * 【通用方法】将邮箱从监听中删除
     *
     * @param
     */
    public void delTaskListener(String bindId) {
        List<MailListener> threads = mailTask.getThreads();
        for (int i = 0; i < threads.size(); i++) {
            MailListener listener = threads.get(i);
            if (listener.getBindEmail().getId().equals(bindId)) {
                listener.closeTask();
                threads.remove(listener);
                break;
            }
        }
    }
}

