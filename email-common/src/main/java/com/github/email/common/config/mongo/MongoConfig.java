package com.github.email.common.config.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class MongoConfig {

    private final MongoOptionProperties mongoOptionProperties;

    public MongoClient getMongoClientSettings(String uri) {

        return MongoClients.create(MongoClientSettings.builder().applyToConnectionPoolSettings(builder -> {
                    builder.maxSize(mongoOptionProperties.getMaxConnectionPerHost());
                    builder.minSize(mongoOptionProperties.getMinConnectionPerHost());
                    builder.maxWaitTime(mongoOptionProperties.getMaxWaitTime(), TimeUnit.MILLISECONDS);
                    builder.maxConnectionIdleTime(mongoOptionProperties.getMaxConnectionIdleTime(), TimeUnit.MILLISECONDS);
                    builder.maxConnectionLifeTime(mongoOptionProperties.getMaxConnectionLifeTime(), TimeUnit.MILLISECONDS);
                })
                .applyToClusterSettings(builder -> {
                    builder.serverSelectionTimeout(mongoOptionProperties.getServerSelectionTimeout(), TimeUnit.MILLISECONDS);
                    builder.localThreshold(mongoOptionProperties.getLocalThreshold(), TimeUnit.MILLISECONDS);
                })
                .applyToServerSettings(builder -> {
                    builder.heartbeatFrequency(mongoOptionProperties.getHeartbeatFrequency(), TimeUnit.MILLISECONDS);
                    builder.minHeartbeatFrequency(mongoOptionProperties.getHeartbeatFrequency(), TimeUnit.MILLISECONDS);

                })
                .applyToSocketSettings(builder -> {
                    builder.readTimeout(mongoOptionProperties.getSocketTimeout(), TimeUnit.MILLISECONDS);
                    builder.receiveBufferSize(mongoOptionProperties.getReceiveBufferSize());
                    builder.sendBufferSize(mongoOptionProperties.getSendBufferSize());
                    builder.connectTimeout(mongoOptionProperties.getConnectTimeout(), TimeUnit.MILLISECONDS);
                    builder.readTimeout(mongoOptionProperties.getSocketTimeout(), TimeUnit.MILLISECONDS);
                })

                .applyConnectionString(new ConnectionString(uri)).build());
    }

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(factory());
    }

    @Bean
    public MongoDatabaseFactory factory() {
        return new SimpleMongoClientDatabaseFactory(getMongoClientSettings(uri), "email");
    }

    @Bean(name = "TRANSACTION_MANAGER")
    public MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
        return new MongoTransactionManager(factory);
    }
}