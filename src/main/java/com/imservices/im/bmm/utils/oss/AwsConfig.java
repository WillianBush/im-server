package com.imservices.im.bmm.utils.oss;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class AwsConfig {

    private S3Model s3Model;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(s3Model.getAccessKeyId(), s3Model.getAccessKeySecret());
        return AmazonS3ClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_1).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
    }
}
