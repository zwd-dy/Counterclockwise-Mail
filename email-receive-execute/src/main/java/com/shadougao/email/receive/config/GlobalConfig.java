package com.shadougao.email.receive.config;

import com.shadougao.email.receive.execute.MailTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class GlobalConfig {

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
