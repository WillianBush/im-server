package com.imservices.im.bmm.service;


import com.imservices.im.bmm.utils.oss.UploadResp;

import java.io.InputStream;

public interface FileSystemService {

    void deleteFile( String folder, String filePath);

    void deleteFile( String filePath);

    UploadResp uploadObject(InputStream inputStream, String fileName, String folder, Long fileSize);

    String getEndpoint();

    String removeBucketName(String uriWithBucketName);

    String getDomain();
}
