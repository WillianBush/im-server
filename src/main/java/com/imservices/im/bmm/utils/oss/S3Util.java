package com.imservices.im.bmm.utils.oss;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@Data
public class S3Util {

    private AmazonS3 s3;

    private S3Model s3Model;

    /**
     * 列出一个s3桶内的文件
     */
    public void listFiles() {
        //可以加入文件名前缀当作搜索条件
        String prefix = "";
        ObjectListing objectListing = s3.listObjects(s3Model.getBucketname());
        List<S3ObjectSummary> s3ObjectSummaryList = objectListing.getObjectSummaries();
        s3ObjectSummaryList.forEach(item -> {
            //文件路径及名称-如果是文件夹，以"/"标识路径
            System.out.print(item.getKey() + ",");
            //文件UUID
            System.out.println(item.getETag());
        });
    }

    /**
     * 上传一个文件到s3桶
     * 注意1：如果key相同，则替换
     * 注意2：如果key是一个层次路径，那么s3会自动创建相应文件夹
     */
    public UploadResp uploadFile(InputStream inputStream, String fileName, String folder) {
        log.info("上传文件到s3的请求参数为:fileName:{},folder:{}",fileName,folder);
        String resultStr = null;
        String ossFilePath=null;
        try{
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(inputStream.available());
            //指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            //指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            //指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            //文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            //如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(fileName));
            //指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + inputStream.available() + "Byte.");

            PutObjectRequest putObjectRequest = new PutObjectRequest(s3Model.getBucketname(), folder+"/" + fileName, inputStream,metadata);
            //设置权限属性等-非必需
//        putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            //上传
            PutObjectResult putObjectResult = s3.putObject(putObjectRequest);
            log.info("获取上传后文件路径地址");
            ossFilePath = "https://" + s3Model.getEndpoint() + "/"+folder+"/" + fileName;
            resultStr = putObjectResult.getETag();
        }catch (Exception e){
            log.info("执行上传和获取文件链接异常:{}",e.getMessage());
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new UploadResp(ossFilePath,resultStr);
    }
    /**
     * @param @param  remoteFileName 文件名
     * @param @return
     * @param @throws IOException    设定文件
     * @return String    返回类型
     * @throws
     * @Title: getUrlFromS3
     * @Description: 获取文件的url
     */
    public String getUrlFromS3(String key) {
        try {
            GeneratePresignedUrlRequest httpRequest = new GeneratePresignedUrlRequest(s3Model.getBucketname(), key);
            return s3.generatePresignedUrl(httpRequest).toString();//临时链接

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void deleteFile(String filePath) {
        s3.deleteObject(s3Model.getBucketname(), filePath);
    }


    public void deleteFile(String folder, String filePath) {
        s3.deleteObject(s3Model.getBucketname(), folder + "/" + filePath);
    }

    /**
     * 获取一个文件
     *
     * @throws IOException
     */
    public S3Object downloadFile(String filePath) throws IOException {
        //获取文件
        S3Object s3Object = s3.getObject(s3Model.getBucketname(), filePath);
        //s3文件输入流，继承了FilterInputStream
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        //文件属性等数据
        ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
        //文件的key-可以用来获取文件名
        System.out.println(s3Object.getKey());
        //桶名
        System.out.println(s3Object.getBucketName());
        System.out.println(s3Object.getRedirectLocation());
        System.out.println(s3Object.getTaggingCount());
        return s3Object;
    }

    /**
     * 获取一个文件,并打印出来
     * @param content 目录
     * @param remoteFileName 需要下载的文件名
     * @throws IOException
     */
    public void downloadFileWithPrint(String content, String remoteFileName) throws IOException {
        String key = content + "/" + remoteFileName;
        S3Object s3Object = s3.getObject(s3Model.getBucketname(), key);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        //如果是txt文件，可以把内容打印出来
        StringBuffer stringBuffer = new StringBuffer();
        byte[] buffer = new byte[1024];
        while ((s3ObjectInputStream.read(buffer)) != -1) {
            stringBuffer.append(new String(buffer, "UTF-8"));
        }
        s3ObjectInputStream.close();
        System.out.println(stringBuffer.toString().trim());
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     * @param fileName 文件名
     * @return 文件的contentType
     */
    public String getContentType(String fileName){
        //文件的后缀名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if(".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if(".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if(".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension)  || ".png".equalsIgnoreCase(fileExtension) ) {
            return "image/jpeg";
        }
        if(".html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if(".txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if(".vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if(".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if(".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if(".xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        //默认返回类型
        return "image/jpeg";
    }
}
