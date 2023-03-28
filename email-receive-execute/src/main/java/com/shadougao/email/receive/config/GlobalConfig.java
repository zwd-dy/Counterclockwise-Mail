package com.shadougao.email.receive.config;

import com.shadougao.email.receive.execute.MailListener;
import com.shadougao.email.receive.execute.MailTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

@Configuration
public class GlobalConfig {

    public String nodeName;
    @Value("${email.listener.user-online-cycle}")
    public Integer userOnlineCycle;
    @Value("${email.listener.normal-cycle}")
    public Integer normalCycle;
}
