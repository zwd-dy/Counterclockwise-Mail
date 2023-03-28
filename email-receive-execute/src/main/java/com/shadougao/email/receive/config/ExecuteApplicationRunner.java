package com.shadougao.email.receive.config;

import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.RedisResult;
import com.shadougao.email.entity.RedisResultEnum;
import com.shadougao.email.receive.execute.MailTask;
import com.shadougao.email.receive.listener.RedisExecuteListener;
import com.shadougao.email.receive.utils.GetBeanUtil;
import com.shadougao.email.receive.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ExecuteApplicationRunner implements ApplicationRunner {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisConfig redisConfig;


    /*
            发：executeChannel
            收：mainChannel
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 通知主程序，子节点加入
        redisUtil.publist(redisConfig.executeChannel,new RedisResult(RedisResultEnum.CONNECT_MAIN,"hello"));
    }

    @Bean
    public MailTask mailTask() {
        MailTask mailTask = new MailTask();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                0,
                Integer.MAX_VALUE,
                60L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>()
        );
        mailTask.setPoolExecutor(threadPoolExecutor);
        mailTask.setThreads(new ArrayList<>());
        return mailTask;
    }

}
