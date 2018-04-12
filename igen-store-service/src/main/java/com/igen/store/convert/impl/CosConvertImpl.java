package com.igen.store.convert.impl;

import com.igen.store.config.StoreConfigProperties;
import com.igen.store.convert.IConvert;
import com.igen.store.domain.StoreRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Desc: 腾讯对象转换校验
 * User: wangmin
 * Date: 2017/12/5
 * Time: 下午3:46
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "store.platform",havingValue = "cos")
public class CosConvertImpl implements IConvert<PutObjectRequest> {

    @Autowired
    private StoreConfigProperties storeConfigProperties;

    @Override
    public PutObjectRequest convert(StoreRequest storeRequest) throws Exception {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(storeRequest.getStreamLength());
        return new PutObjectRequest(storeRequest.getBucketName(), storeRequest.getPathKey(), storeRequest.getInputStream(), objectMetadata);
    }

    @Override
    public String validate(StoreRequest storeRequest) {
        if (StringUtils.isBlank(storeRequest.getPathKey())) {
            return "文件路径key为空";
        }
        if (storeRequest.getStreamLength() == null) {
            return "输入流长度为空";
        }
        if (storeRequest.getInputStream() == null) {
            return "输入流为空";
        }
        if (StringUtils.isBlank(storeRequest.getFileName())) {
            return "文件名为空";
        }
        if (StringUtils.isBlank(storeRequest.getBucketName())) {
            storeRequest.setBucketName(storeConfigProperties.getDefaultBucket());
        }
        if (StringUtils.startsWith(storeRequest.getPathKey(), "/") || StringUtils.startsWith(storeRequest.getPathKey(), "\\")) {
            storeRequest.setPathKey(StringUtils.substring(storeRequest.getPathKey(), 1));
        }
        if (!StringUtils.endsWith(storeRequest.getPathKey(),"/")) {
            storeRequest.setPathKey(storeRequest.getPathKey() + "/" + storeRequest.getFileName());
        } else {
            storeRequest.setPathKey(storeRequest.getPathKey() + storeRequest.getFileName());
        }
        return null;
    }

    @Override
    public String getObjectUrl(String bucketName, String pathKey) {
        String url;
        if (storeConfigProperties.getCdn()) {//CDN加速域名地址
            url = storeConfigProperties.getCdnUrl();
        } else {//默认域名地址
            url = storeConfigProperties.getUrl();
        }
        url = StringUtils.replace(url, REPLACE_BUCKET, bucketName);
        return url + pathKey;
    }
}
