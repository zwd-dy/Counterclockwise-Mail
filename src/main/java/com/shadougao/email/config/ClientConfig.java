package com.shadougao.email.config;

import com.shadougao.email.execute.SendMailExecutor;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.NettyCustomizer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;


@Configuration
public class ClientConfig {

    /**
     * 给lettuce添加心跳包，防止redis断开
     *
     * @return
     */
    @Bean
    public ClientResources clientResources() {

        NettyCustomizer nettyCustomizer = new NettyCustomizer() {

            @Override
            public void afterChannelInitialized(Channel channel) {
                channel.pipeline().addLast(
                        new IdleStateHandler(60, 0, 0));
                channel.pipeline().addLast(new ChannelDuplexHandler() {
                    @Override
                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                        if (evt instanceof IdleStateEvent) {
                            ctx.disconnect();
                        }
                    }
                });
            }

            @Override
            public void afterBootstrapInitialized(Bootstrap bootstrap) {
            }

        };
        return ClientResources.builder().nettyCustomizer(nettyCustomizer).build();
    }

    /**
     * 发邮件线程池
     * @return
     */
    @Bean
    public SendMailExecutor sendMailExecutor() {
        SendMailExecutor sendMailExecutor = new SendMailExecutor();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                4,
                10,
                100L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100));
        sendMailExecutor.setExecutorService(executor);
        return sendMailExecutor;
    }
}
