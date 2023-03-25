package com.shadougao.email.receive.config;

import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.RedisResult;
import com.shadougao.email.entity.RedisResultEnum;
import com.shadougao.email.receive.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
}
