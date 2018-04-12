package com.igen.store.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.igen.bean.ApiResult;
import com.igen.bean.Result;
import com.igen.constant.EnumConstans.ApiStatus;
import com.igen.store.config.StoreConfigProperties;
import com.igen.store.convert.IConvert;
import com.igen.store.domain.StoreRequest;
import com.igen.store.service.IStoreService;
import com.igen.utils.JsonUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.Bucket;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Desc: 腾讯cos存储服务
 * User: wangmin
 * Date: 2017/12/4
 * Time: 上午10:47
 */
@Slf4j
@Service("storeService")
@ConditionalOnProperty(name = "store.platform", havingValue = "cos")
public class CosStoreServiceImpl implements IStoreService {

    @Autowired
    private StoreConfigProperties storeConfigProperties;

    @Autowired
    private IConvert<PutObjectRequest> cosConvertImpl;

    private static COSClient cosClient;

    @PostConstruct
    @Override
    public void init() {
        //设置认证信息
        COSCredentials cred = new BasicCOSCredentials(storeConfigProperties.getCredential().getAppId(), storeConfigProperties.getCredential().getSecrectId(), storeConfigProperties.getCredential().getSecrectKey());

        //设置bucket的区域
        ClientConfig clientConfig = new ClientConfig(new Region(storeConfigProperties.getRegion()));

        //生成cos客户端
        cosClient = new COSClient(cred, clientConfig);
    }

    @PreDestroy
    @Override
    public void destroy() {
        cosClient.shutdown();
    }

    @Override
    public Result createBucket(String bucketName) throws Exception {
        log.info("create bucket {}", bucketName);
        if (StringUtils.isBlank(bucketName)) {
            return new Result(ApiStatus.PARAM_ERROR.getCode(), "桶位名称不能为空");
        }
        boolean result = cosClient.doesBucketExist(bucketName);
        if (result) {
            return new Result(ApiStatus.FAIL.getCode(), "桶位已经存在");
        } else {
            Bucket bucket = cosClient.createBucket(bucketName);
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
        String errorMsg = cosConvertImpl.validate(storeRequest);
        if (StringUtils.isNotEmpty(errorMsg)) {
            log.error(errorMsg);
            return new ApiResult(ApiStatus.PARAM_ERROR.getCode(), errorMsg);
        }

        //协议对象转换
        PutObjectRequest putObjectRequest = cosConvertImpl.convert(storeRequest);

        //cos存储
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        if (putObjectResult != null && StringUtils.isNotBlank(putObjectResult.getETag())) {
            log.info("upload object on cos done,result {}", JsonUtils.toJson(putObjectResult));
            storeRequest.getInputStream().close();
            JSONObject data = new JSONObject();
            data.put(URL, cosConvertImpl.getObjectUrl(storeRequest.getBucketName(), storeRequest.getPathKey()));
            return ApiResult.SUCCESS().setData(data);
        } else {
            return ApiResult.FAIL();
        }
    }
}
