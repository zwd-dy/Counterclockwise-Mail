package com.shadougao.email.receive.listener;

import com.shadougao.email.annotation.RedisChannelListener;
import com.shadougao.email.annotation.RedisResultCode;
import com.shadougao.email.dao.mongo.SysEmailPlatformDao;
import com.shadougao.email.entity.RedisResult;
import com.shadougao.email.entity.RedisResultEnum;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.receive.config.RedisConfig;
import com.shadougao.email.receive.execute.MailListener;
import com.shadougao.email.receive.execute.MailTask;
import com.shadougao.email.receive.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 执行器监听
 */
@Component
@RedisChannelListener
public class RedisExecuteListener {

    // key前缀
    public static final String KEY_PREFIX = "email:task:";
    public static final String KEY_NODE_LIST = KEY_PREFIX + "node_list";
    public static final String KEY_NODE_TASK = KEY_PREFIX + "node_task";

    public String name;
    /*
        发：executeChannel
        收：mainChannel
 */

    @Autowired
    private MailTask mailTask;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisConfig redisConfig;
    @Autowired
    private SysEmailPlatformDao platformDao;

    /**
     * 连接主程序成功，开始从缓存获取任务并执行
     * @param
     */
    @RedisResultCode(RedisResultEnum.CONNECT_SUCCESS)
    public void connectSuccess(RedisResult result) {
        name = (String) result.getData();

        // 获取任务
        // 获取邮箱平台列表
        List<SysEmailPlatform> platforms = platformDao.getAll();
        List<UserBindEmail> bindEmails = (List<UserBindEmail>) redisUtil.hget(KEY_NODE_TASK, name);
        // 清空线程管理
        mailTask.getThreads().clear();
        for (UserBindEmail bindEmail : bindEmails) {
            MailListener listener = new MailListener();
            // 设置监控邮箱
            listener.setBindEmail(bindEmail);
            // 设置对应邮箱平台
            listener.setPlatform(platforms.stream().filter(p -> bindEmail.getPlatformId().equals(p.getId())).findFirst().get());
            // 设置轮询周期（秒级）
            listener.setSleepTime(10);
            // 放入到线程管理
            mailTask.getThreads().add(listener);
            // 执行
            mailTask.getPoolExecutor().execute(listener);
        }
    }


}

