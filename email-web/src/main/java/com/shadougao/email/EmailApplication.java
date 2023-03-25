package com.shadougao.email;


import com.shadougao.email.config.security.auth.rest.AnonymousGetMapping;
import com.shadougao.email.config.security.bean.SpringContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RestController;

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

}