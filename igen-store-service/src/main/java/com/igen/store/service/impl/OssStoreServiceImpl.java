package com.igen.store.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.igen.bean.ApiResult;
import com.igen.bean.Result;
import com.igen.constant.EnumConstans.ApiStatus;
import com.igen.store.config.StoreConfigProperties;
import com.igen.store.convert.IConvert;
import com.igen.store.domain.StoreRequest;
import com.igen.store.service.IStoreService;
import com.igen.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Desc: 阿里oss存储服务
 * User: wangmin
 * Date: 2017/12/5
 * Time: 下午7:23
 */
@Slf4j
@Service("storeService")
@ConditionalOnProperty(name = "store.platform", havingValue = "oss")
public class OssStoreServiceImpl implements IStoreService {

    @Autowired
    private StoreConfigProperties storeConfigProperties;

    @Autowired
    private IConvert<PutObjectRequest> ossConvertImpl;

    private static OSSClient ossClient;

    @PostConstruct
    @Override
    public void init() {

        //生成oss客户端
        ossClient = new OSSClient(storeConfigProperties.getEndpoint(), storeConfigProperties.getCredential().getSecrectId(), storeConfigProperties.getCredential().getSecrectKey());
    }

    @PreDestroy
    @Override
    public void destroy() {
        ossClient.shutdown();
    }

    @Override
    public Result createBucket(String bucketName) throws Exception {
        log.info("create bucket {}", bucketName);
        if (StringUtils.isBlank(bucketName)) {
            return new Result(ApiStatus.PARAM_ERROR.getCode(), "桶位名称不能为空");
        }
        boolean result = ossClient.doesBucketExist(bucketName);
        if (result) {
            return new Result(ApiStatus.FAIL.getCode(), "桶位已经存在");
        } else {
            Bucket bucket = ossClient.createBucket(bucketName);
            if (bucket != null) {
                log.info("create bucket done,bucket {}", JsonUtils.toJson(bucket));
                return new Result(ApiStatus.SUCCESS.getCode(), "桶位创建成功");
            } else {
                return new Result(ApiStatus.FAIL.getCode(), "桶位创建失败");
            }
        }
    }

    @Override
    public ApiResult uploadObject(StoreRequest storeRequest) throws Exception {
        log.info("upload object {}", storeRequest);

        //参数校验
        String errorMsg = ossConvertImpl.validate(storeRequest);
        if (StringUtils.isNotEmpty(errorMsg)) {
            log.error(errorMsg);
            return new ApiResult(ApiStatus.PARAM_ERROR.getCode(), errorMsg);
        }

        //协议对象转换
        PutObjectRequest putObjectRequest = ossConvertImpl.convert(storeRequest);

        //oss存储
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(storeRequest.getStreamLength());
        PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
        if (putObjectResult != null && StringUtils.isNotBlank(putObjectResult.getETag())) {
            log.info("upload object on oss done,result {}", JsonUtils.toJson(putObjectResult));
            storeRequest.getInputStream().close();
            JSONObject data = new JSONObject();
            data.put(URL, ossConvertImpl.getObjectUrl(storeRequest.getBucketName(), storeRequest.getPathKey()));
            return ApiResult.SUCCESS().setData(data);
        } else {
            return ApiResult.FAIL();
        }
    }
}
