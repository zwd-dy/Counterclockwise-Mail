package com.github.email.common.config.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mongo.config")
@Getter
@Setter
public class MongoOptionProperties {

    private Integer minConnectionPerHost;
    private Integer maxConnectionPerHost;
    private Integer threadsAllowedToBlockForConnectionMultiplier;
    private Integer serverSelectionTimeout;
    private Integer maxWaitTime;
    private Integer maxConnectionIdleTime;
    private Integer maxConnectionLifeTime;
    private Integer connectTimeout;
    private Integer socketTimeout;
    private Boolean socketKeepAlive;
    private Boolean sslEnabled;
    private Boolean sslInvalidHostNameAllowed;
    private Boolean alwaysUseMBeans;
    private Integer heartbeatFrequency;
    private Integer minHeartbeatFrequency;
    private Integer heartbeatConnectTimeout;
    private Integer heartbeatSocketTimeout;
    private Integer localThreshold;
    private Integer receiveBufferSize;
    private Integer sendBufferSize;

    private MongoOptionProperties() {
    }
}