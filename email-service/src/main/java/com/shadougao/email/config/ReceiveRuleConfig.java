package com.shadougao.email.config;

import cn.hutool.core.util.ClassUtil;
import com.shadougao.email.annotation.RedisChannelListener;
import com.shadougao.email.annotation.RedisResultCode;
import com.shadougao.email.rule.ExecuteMap;
import com.shadougao.email.rule.execute.RuleExecute;
import com.shadougao.email.rule.execute.annotation.RuleExecuteType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class ReceiveRuleConfig {

    /**
     * 将所有收信规则的执行器放入Map中
     * @return
     */
    @Bean
    public ExecuteMap executeMap(){
        ExecuteMap executeMap = new ExecuteMap();
        //TODO 扫描包名暂时写死
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation("com.shadougao.email.rule.execute.impl", RuleExecuteType.class);
        for (Class<?> aClass : classes) {
            String type = aClass.getAnnotation(RuleExecuteType.class).value();
            try {
                executeMap.put(type, (RuleExecute) aClass.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return executeMap;
    }
}
