package com.shadougao.email.common.utils;

import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;


/**
 * 请求日志
 */
@Aspect
@Slf4j
@Component
public class WebLogAspect {

    @Pointcut("execution(public * com.shadougao.email.web.controller.*.*(..)))")
    public void webLog() {

    }

    @Before("webLog()")
    public void webLog(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;
        // 获取当前请求
        HttpServletRequest request = attributes.getRequest();
        // 获取浏览器信息
        String header = request.getHeader("User-Agent");
        // 转成UserAgent对象
        UserAgent browser = UserAgent.parseUserAgentString(header);
        // 获取浏览器版本号
        Version version = browser.getBrowser().getVersion(request.getHeader("User-Agent"));
        // 获取系统信息
        OperatingSystem os = browser.getOperatingSystem();

        // 记录真正的内容
        StringBuilder sb = new StringBuilder();
        sb.append("USER: ").append(SecurityUtils.getCurrentUsername())
                .append(" | URL: ").append(request.getRequestURL().toString())
                .append(" | HTTP: ").append(request.getMethod())
                .append(" | IP: ").append(IpUtils.getIp(request))
                .append(" | CLASS: ").append(joinPoint.getSignature().getDeclaringTypeName()).append(".").append(joinPoint.getSignature().getName());
        if (browser.getBrowser() != null) {
            sb.append(" | BROWSER: ").append(browser.getBrowser().getName());
        }
        if (version != null) {
            sb.append("/").append(version.getVersion());
        }
        if (os != null) {
            sb.append(" | OS: ").append(os.getName());
        }
        log.info(sb.toString());
    }

    @AfterThrowing(pointcut = "webLog()", throwing = "e")
    public void exceptionLog(JoinPoint joinPoint, Throwable e) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;
        // 获取当前请求
        HttpServletRequest request = attributes.getRequest();
        String sb = "USER: " + SecurityUtils.getCurrentUsername() +
                " | URL: " + request.getRequestURL().toString() +
                " | HTTP: " + request.getMethod() +
                " | IP: " + IpUtils.getIp(request) +
                " | CLASS: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() +
                " | ARGS: " + Arrays.toString(joinPoint.getArgs()) + "." + joinPoint.getSignature().getName() +
                " | ERROR_NAME: " + e.getClass().getName() +
                " | ERROR_TIME: " + LocalDateTime.now();
        log.error(sb);
    }

}
