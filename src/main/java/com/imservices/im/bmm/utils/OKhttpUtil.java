package com.imservices.im.bmm.utils;

import com.alibaba.fastjson.JSONObject;
import com.imservices.im.bmm.exception.RRException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class OKhttpUtil {

    private static final MediaType mediaType = MediaType.parse("application/json");


    private static final OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30000L, TimeUnit.MILLISECONDS)
            .readTimeout(50000L, TimeUnit.MILLISECONDS);


    /**
     * get请求
     *
     * @param url
     * @return
     */
    public static String httpGet(String url, Map<String, String> queryParams, Map<String, String> header) throws IOException {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> param : queryParams.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        Request request = new Request.Builder().url(httpBuilder.build())
                .headers(Headers.of(header))
                .build();
        Response response = okHttpClient.build().newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            if (!ObjectUtils.isEmpty(body)){
                String resp = body.string();
                log.info("httpGet 请求成功 ，resp：{}",resp);
                return resp;
            }
            return null;
        }
        log.error("httpGet 请求失败,url:{},params:{},header:{};response:{}", url, JSONObject.toJSONString(queryParams), header,response);
        throw new RRException("httpGet请求失败");
    }

    public static String httpGet(String url, Map<String, String> queryParams) throws IOException {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> param : queryParams.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        Request request = new Request.Builder().url(httpBuilder.build())
                .build();
//        log.info("httpGet 请求,url:{},params:{}", url, JSONObject.toJSONString(queryParams));
        Response response = okHttpClient.build().newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            if (!ObjectUtils.isEmpty(body)){
                String resp = body.string();
//                log.info("httpGet 请求成功 ，resp：{}",resp);
                return resp;
            }
            return null;
        }
//        log.error("httpGet 请求失败,url:{},params:{};response:{}", url, JSONObject.toJSONString(queryParams),response);
        throw new RRException("httpGet请求失败");
    }

    /**
     * post请求
     *
     * @param url
     * @param json
     */
    public String httpPostJson(String url, Object json, Map<String, String> header) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType, JSONObject.toJSONString(json));
        Request request = new Request.Builder().url(url)
                .post(requestBody)
                .headers(Headers.of(header))
                .build();
        Response response = okHttpClient.build().newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            if (!ObjectUtils.isEmpty(body)){
                String resp = body.string();
                log.info("httpPOST 请求成功 ，resp：{}",resp);
                return resp;
            }
        }
        log.error("httpPOST 请求失败,url:{},params:{},header:{};response:{}", url, JSONObject.toJSONString(json), header,response);
        throw new RRException("httpPostJson请求失败");
    }

    public String httpPostJson(String url, Object json) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType, JSONObject.toJSONString(json));
        Request request = new Request.Builder().url(url)
                .post(requestBody)
                .build();
        Response response = okHttpClient.build().newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            if (!ObjectUtils.isEmpty(body)){
                String resp = body.string();
//                log.info("httpPOST 请求成功 ，resp：{}",resp);
                return resp;
            }
            return null;
        }
        log.error("httpPOST 请求失败,url:{},params:{};response:{}", url, JSONObject.toJSONString(json),response);
        throw new RRException("httpPostJson请求失败");
    }
}
