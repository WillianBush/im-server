package com.imservices.im.bmm.utils.oss;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aliyun.oss")
@Data
@ToString
public class OSSModel {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String roleArn;

    private String bucketname;

    private String appPoint;

    private String domain;



}
