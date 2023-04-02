package com.shadougao.email.config;

import com.shadougao.email.common.utils.GetBeanUtil;
import com.shadougao.email.execute.ClusterHeartBeat;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class EmailApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 开启cluster心跳包检测
        ClusterHeartBeat clusterHeartBeat = GetBeanUtil.getApplicationContext().getBean(ClusterHeartBeat.class);
        Thread thread = new Thread(clusterHeartBeat);
        thread.setDaemon(true);
        thread.start();
    }
}
