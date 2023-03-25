package com.shadougao.email.execute;

import com.alibaba.fastjson.JSONObject;
import com.shadougao.email.common.utils.GetBeanUtil;
import com.shadougao.email.entity.RedisResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * 新邮件监听
 */
@Component
@Slf4j
public class RedisChannelListenerExecute implements MessageListener {


    @Resource
    private Map<Integer, Method> redisListenerMap;
    /*
            发：mainChannel
            收：executeChannel
     */


    @SneakyThrows
    @Override
    public void onMessage(Message message, byte[] bytes) {
        RedisResult result = JSONObject.parseObject(message.getBody(), RedisResult.class);
        Method method = redisListenerMap.get(result.getCode());
        method.invoke(GetBeanUtil.getApplicationContext().getBean(method.getDeclaringClass()),result);
    }

    //字节码转化为对象
    public  Object getObjectFromBytes(byte[] objBytes) throws Exception {
        if (objBytes == null || objBytes.length == 0) {
            return null;
        }
        ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        return oi.readObject();
    }

}
