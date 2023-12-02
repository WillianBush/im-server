package com.imservices.im.bmm.utils.oss;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.model.analytics.AnalyticsConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AwsS3Bucket {

    public AmazonS3 s3;

    private S3Model s3Model;

    private String region = Regions.AP_NORTHEAST_1.getName();

    @Autowired
    public AwsS3Bucket(AmazonS3 s3, S3Model s3Model) {
        this.s3 = s3;
        this.s3Model = s3Model;
    }

    /**
     * 列出所有的s3桶
     */
    public List<Bucket>  listBuckets() {
        List<Bucket> buckets = s3.listBuckets();
        buckets.forEach(item -> {
            System.out.println(item.toString());
        });
        return buckets;
    }

    /**
     * 创建一个s3桶-默认
     */
    public void createBucketBase() {
        Bucket bucket = s3.createBucket(s3Model.getBucketname());
        System.out.println(bucket.toString());
    }

    /**
     * 创建一个s3桶-带参数
     */
    public void createBucketWithParams() {
        //指定名称和区域
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(s3Model.getBucketname(), region);
        //是否启用对象锁-启用后，阻止删除对象
        createBucketRequest.setObjectLockEnabledForBucket(true);
        Bucket bucket = s3.createBucket(createBucketRequest);
        System.out.println(bucket.toString());
    }

    /**
     * 删除一个s3桶
     */
    public void deleteBucket() {
        DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(s3Model.getBucketname());
        s3.deleteBucket(deleteBucketRequest);
        System.out.println("delete bucket success");
    }

    /**
     * s3桶配置
     */
    public void configBucket() {
        /**
         * 加速配置
         */
        BucketAccelerateConfiguration bucketAccelerateConfiguration = new BucketAccelerateConfiguration(BucketAccelerateStatus.Enabled);
        s3.setBucketAccelerateConfiguration(s3Model.getBucketname(), bucketAccelerateConfiguration);
        /**
         * 权限配置
         */
        //公共访问权限
        SetBucketAclRequest setBucketAclRequest = new SetBucketAclRequest(s3Model.getBucketname(), CannedAccessControlList.Private);
        s3.setBucketAcl(setBucketAclRequest);
        //访问控制列表
        AccessControlList accessControlList = new AccessControlList();
        accessControlList.setRequesterCharged(true);
        accessControlList.setOwner(null);
        SetBucketAclRequest setBucketAclRequest2 = new SetBucketAclRequest(s3Model.getBucketname(), accessControlList);
        s3.setBucketAcl(setBucketAclRequest2);
        /**
         * 分析配置
         */
        AnalyticsConfiguration analyticsConfiguration = new AnalyticsConfiguration();
        analyticsConfiguration.setId(null);
        SetBucketAnalyticsConfigurationRequest setBucketAnalyticsConfigurationRequest = new SetBucketAnalyticsConfigurationRequest(s3Model.getBucketname(), analyticsConfiguration);
        s3.setBucketAnalyticsConfiguration(setBucketAnalyticsConfigurationRequest);
        /**
         * 生命周期配置
         */
        BucketLifecycleConfiguration bucketLifecycleConfiguration = new BucketLifecycleConfiguration();
        List<BucketLifecycleConfiguration.Rule> rules = new ArrayList();
        //需要预先制定规则
        BucketLifecycleConfiguration.Rule rule = new BucketLifecycleConfiguration.Rule().withId(null);
        rules.add(rule);
        bucketLifecycleConfiguration.setRules(rules);
        SetBucketLifecycleConfigurationRequest setBucketLifecycleConfigurationRequest = new SetBucketLifecycleConfigurationRequest(s3Model.getBucketname(), bucketLifecycleConfiguration);
        s3.setBucketLifecycleConfiguration(setBucketLifecycleConfigurationRequest);
        /**
         * 加密配置
         * 当对象存储在s3时默认是加密的
         */
        SetBucketEncryptionRequest setBucketEncryptionRequest = new SetBucketEncryptionRequest();
        setBucketEncryptionRequest.setBucketName(s3Model.getBucketname());
        ServerSideEncryptionConfiguration serverSideEncryptionConfiguration = new ServerSideEncryptionConfiguration();
        //同样，需要预先制定规则
        serverSideEncryptionConfiguration.setRules(null);
        setBucketEncryptionRequest.setServerSideEncryptionConfiguration(serverSideEncryptionConfiguration);
        s3.setBucketEncryption(setBucketEncryptionRequest);
        /**
         * 版本控制配置
         */
        BucketVersioningConfiguration bucketVersioningConfiguration = new BucketVersioningConfiguration();
        bucketVersioningConfiguration.setMfaDeleteEnabled(true);
        bucketVersioningConfiguration.setStatus(BucketVersioningConfiguration.ENABLED);
        SetBucketVersioningConfigurationRequest setBucketVersioningConfigurationRequest = new SetBucketVersioningConfigurationRequest(s3Model.getBucketname(), bucketVersioningConfiguration);
        s3.setBucketVersioningConfiguration(setBucketVersioningConfigurationRequest);

        /**
         * 为s3指定一个策略-s3的策略是唯一的
         */
        s3.setBucketPolicy(null);
        /**
         * 日志记录配置
         */
        s3.setBucketLoggingConfiguration(null);
        /**
         * 通知配置
         */
        s3.setBucketNotificationConfiguration(null);
        /**
         * 复制配置
         */
        s3.setBucketReplicationConfiguration(null);
        /**
         * 标签配置
         */
        s3.setBucketTaggingConfiguration(null);
        /**
         * 静态网站托管配置
         */
        s3.setBucketWebsiteConfiguration(null);
        /**
         * 指标配置
         */
        s3.setBucketMetricsConfiguration(null);

    }
}
