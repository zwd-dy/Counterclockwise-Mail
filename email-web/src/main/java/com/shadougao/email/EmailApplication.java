package com.shadougao.email;


import com.shadougao.email.config.security.auth.rest.AnonymousGetMapping;
import com.shadougao.email.config.security.bean.SpringContextHolder;
import com.shadougao.email.execute.MailExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController
/* 开启审计 */
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class EmailApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmailApplication.class, args);
        System.out.println(
                " ███▄    █    ███▄ ▄███▓   ▄████▄     ▄▄▄▄\n" +
                        " ██ ▀█   █   ▓██▒▀█▀ ██▒  ▒██▀ ▀█    ▓█████▄\n" +
                        "▓██  ▀█ ██  ▒▓██    ▓██░  ▒▓█    ▄   ▒██▒ ▄██\n" +
                        "▓██▒  ▐▌██  ▒▒██    ▒██   ▒▓▓▄ ▄██  ▒▒██░█▀\n" +
                        "▒██░   ▓██  ░▒██▒   ░██▒  ▒ ▓███▀   ░░▓█  ▀█▓\n" +
                        "░ ▒░   ▒ ▒   ░ ▒░   ░  ░  ░ ░▒ ▒    ░░▒▓███▀▒\n" +
                        "░ ░░   ░ ▒  ░░  ░      ░    ░  ▒     ▒░▒   ░\n" +
                        "   ░   ░ ░   ░      ░     ░           ░    ░\n" +
                        "         ░          ░     ░ ░         ░\n" +
                        "                      ░                  ░\n" +
                        "                                 Author:  miku & dd\n"
        );
    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    @Bean
    public ServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory fa = new TomcatServletWebServerFactory();
        fa.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "[]{}"));
        return fa;
    }

    @AnonymousGetMapping("/")
    public String index() {
        return "Backend service started successfully";
    }


    /**
     * 发邮箱和解析邮箱的线程池
     * @return
     */
    @Bean
    public MailExecutor sendMailExecutor() {
        // 发邮箱线程池
        MailExecutor mailExecutor = new MailExecutor();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                4,
                10,
                100L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100));
        // 解析邮箱线程池
        ThreadPoolExecutor parseExecutor = new ThreadPoolExecutor(
                4,
                10,
                100L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100));
        mailExecutor.setSendExecutorService(executor);
        mailExecutor.setParseExecutorService(parseExecutor);
        return mailExecutor;
    }

}
