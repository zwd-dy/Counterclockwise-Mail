package com.shadougao.email.receive.execute;

import com.alibaba.fastjson.JSONObject;
import com.shadougao.email.entity.RedisResult;
import com.shadougao.email.receive.utils.GetBeanUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;


@Component
@Slf4j
public class RedisChannelListenerExecute implements MessageListener {

    @Resource
    private Map<Integer, Method> redisListenerMap;

    @SneakyThrows
    @Override
    public void onMessage(Message message, byte[] bytes) {
        RedisResult result = JSONObject.parseObject(message.getBody(), RedisResult.class);
        Method method = redisListenerMap.get(result.getCode());
//        method.invoke(GetBeanUtil.getApplicationContext().getBean(method.getDeclaringClass()), result);
        method.invoke(method.getDeclaringClass().newInstance(), result);
    }

}
