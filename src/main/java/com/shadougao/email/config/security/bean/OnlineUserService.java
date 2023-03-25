package com.shadougao.email.config.security.bean;

import com.shadougao.email.common.utils.EncryptUtils;
import com.shadougao.email.common.utils.IpUtils;
import com.shadougao.email.common.utils.RedisUtil;
import com.shadougao.email.entity.OnlineUserDto;
import com.shadougao.email.entity.dto.JwtUserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OnlineUserService {

    private final SecurityProperties properties;
    private final RedisUtil redisUtil;

    public OnlineUserService(SecurityProperties properties, RedisUtil redisUtil) {
        this.properties = properties;
        this.redisUtil = redisUtil;
    }

    /**
     * 保存在线用户信息
     *
     * @param jwtUserDto /
     * @param token      /
     * @param request    /
     */
    public void save(JwtUserDto jwtUserDto, String token, HttpServletRequest request) {
        String ip = IpUtils.getIp(request);
        String browser = IpUtils.getBrowser(request);
        String address = IpUtils.getLocalCityInfo(ip);
        OnlineUserDto onlineUserDto = null;
        try {
            onlineUserDto = new OnlineUserDto(jwtUserDto.getUsername(), browser, ip, address, EncryptUtils.desEncrypt(token), new Date());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        redisUtil.set(properties.getOnlineKey() + token, onlineUserDto, properties.getTokenValidityInSeconds() / 1000);
    }

    /**
     * 查询全部数据，不分页
     *
     * @param filter /
     * @return /
     */
    public List<OnlineUserDto> getAll(String filter) {
        List<String> keys = redisUtil.scan(properties.getOnlineKey() + "*");
        Collections.reverse(keys);
        List<OnlineUserDto> onlineUserDtos = new ArrayList<>();
        for (String key : keys) {
            OnlineUserDto onlineUserDto = (OnlineUserDto) redisUtil.get(key);
            if (StringUtils.isNotBlank(filter)) {
                if (onlineUserDto.toString().contains(filter)) {
                    onlineUserDtos.add(onlineUserDto);
                }
            } else {
                onlineUserDtos.add(onlineUserDto);
            }
        }
        onlineUserDtos.sort((o1, o2) -> o2.getLoginTime().compareTo(o1.getLoginTime()));
        return onlineUserDtos;
    }

    /**
     * 踢出用户
     *
     * @param key /
     */
    public void kickOut(String key) {
        key = properties.getOnlineKey() + key;
        redisUtil.del(key);
    }

    /**
     * 退出登录
     *
     * @param token /
     */
    public void logout(String token) {
        String key = properties.getOnlineKey() + token;
        redisUtil.del(key);
    }

    /**
     * 查询用户
     *
     * @param key /
     * @return /
     */
    public OnlineUserDto getOne(String key) {
        return (OnlineUserDto) redisUtil.get(key);
    }

    /**
     * 根据用户名强退用户
     *
     * @param username /
     */
    @Async
    public void kickOutForUsername(String username) throws Exception {
        List<OnlineUserDto> onlineUsers = getAll(username);
        for (OnlineUserDto onlineUser : onlineUsers) {
            if (onlineUser.getUserName().equals(username)) {
                String token = EncryptUtils.desDecrypt(onlineUser.getKey());
                kickOut(token);
            }
        }
    }

}
