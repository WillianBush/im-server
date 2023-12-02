package com.imservices.im.bmm.service.impl;

import com.imservices.im.bmm.service.FileSystemService;
import com.imservices.im.bmm.utils.oss.OssUtil;
import com.imservices.im.bmm.utils.oss.S3Util;
import com.imservices.im.bmm.utils.oss.UploadResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;

@Service
@Slf4j
public class FileSystemServiceImpl implements FileSystemService {

    public static final String OSS = "oss";

    public static final String S3 = "s3";

    @Value("${file-system-active}")
    private String fileSystemActive;

    @Resource
    private OssUtil ossUtil;

    @Resource
    private S3Util s3Util;


    @Override
    public void deleteFile(String folder, String filePath) {
        if (OSS.equals(fileSystemActive)){
            ossUtil.deleteFile(folder,filePath);
        }else {
            s3Util.deleteFile(folder,filePath);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        if (OSS.equals(fileSystemActive)){
            ossUtil.deleteFile(filePath);
        }else {
            s3Util.deleteFile(filePath);
        }
    }

    @Override
    public UploadResp uploadObject(InputStream inputStream, String fileName, String folder, Long fileSize) {
        if (OSS.equals(fileSystemActive)){
           return ossUtil.uploadObjectToOSS(inputStream,fileName,folder,fileSize);
        }
        return  s3Util.uploadFile(inputStream,fileName,folder);
    }

    @Override
    public String getEndpoint() {
        if (OSS.equals(fileSystemActive)){
            return ossUtil.getOssModel().getEndpoint();
        }
        return  s3Util.getS3Model().getEndpoint();
    }

    @Override
    public String removeBucketName(String uriWithBucketName) {
        if (OSS.equals(fileSystemActive)){
            return uriWithBucketName;
        }
        return uriWithBucketName.replaceFirst("/"+s3Util.getS3Model().getBucketname(),"");
    }

    @Override
    public String getDomain() {
        if (OSS.equals(fileSystemActive)){
            return ossUtil.getOssModel().getDomain();
        }
        return  s3Util.getS3Model().getDomain();
    }
}
