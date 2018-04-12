package com.igen.store.service;

import com.aliyun.oss.model.PutObjectRequest;
import com.igen.bean.ApiResult;
import com.igen.bean.Result;
import com.igen.constant.EnumConstans.ApiStatus;
import com.igen.store.BaseTest;
import com.igen.store.config.StoreConfigProperties;
import com.igen.store.convert.IConvert;
import com.igen.store.domain.StoreRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;

/**
 * Desc: 阿里oss服务测试
 * User: wangmin
 * Date: 2017/12/7
 * Time: 下午12:33
 */
@Slf4j
@ActiveProfiles("oss")
@SpringBootTest
@RunWith(SpringRunner.class)
public class OssServiceTests extends BaseTest {

    @Autowired
    private IConvert<PutObjectRequest> ossConvertImpl;

    @Autowired
    private IStoreService storeService;

    @Autowired
    protected StoreConfigProperties storeConfigProperties;

    private StoreRequest storeRequest;

    @Before
    public void before() throws Exception {
        Resource resource = new ClassPathResource(FILE_NAME);
        storeRequest = new StoreRequest();
        storeRequest.setBucketName(BUCKET_NAME);
        storeRequest.setPathKey(PATH_KEY);
        storeRequest.setInputStream(resource.getInputStream());
        storeRequest.setStreamLength(resource.contentLength());
    }

    /**
     * 参数校验
     */
    @Test
    public void validateTest() {
        storeRequest.setPathKey(null);
        String result = ossConvertImpl.validate(storeRequest);
        Assert.assertNotNull("对象路径key不能为空", result);
        storeRequest.setPathKey(PATH_KEY);
        InputStream inputStream = storeRequest.getInputStream();
        storeRequest.setInputStream(null);
        result = ossConvertImpl.validate(storeRequest);
        Assert.assertNotNull("输入流不能为空", result);
        storeRequest.setInputStream(inputStream);
        long contentLength = storeRequest.getStreamLength();
        storeRequest.setStreamLength(null);
        result = ossConvertImpl.validate(storeRequest);
        Assert.assertNotNull("输入流长度不能为空", result);
        storeRequest.setStreamLength(contentLength);
        storeRequest.setBucketName(null);
        ossConvertImpl.validate(storeRequest);
        Assert.assertNotNull("存储桶位不能为空", storeRequest.getBucketName());
        storeRequest.setPathKey("/" + PATH_KEY);
        ossConvertImpl.validate(storeRequest);
        Assert.assertFalse(storeRequest.getPathKey().startsWith("/"));
        storeRequest.setPathKey(PATH_KEY);
        result = ossConvertImpl.validate(storeRequest);
        Assert.assertNull(result);
    }

    /**
     * 协议转换
     *
     * @throws Exception
     */
    @Test
    public void convertTest() throws Exception {
        PutObjectRequest putObjectRequest = ossConvertImpl.convert(storeRequest);
        Assert.assertEquals(storeRequest.getBucketName(), putObjectRequest.getBucketName());
        Assert.assertEquals(storeRequest.getPathKey(), putObjectRequest.getKey());
        Assert.assertEquals(storeRequest.getInputStream(), putObjectRequest.getInputStream());
        Assert.assertEquals(storeRequest.getStreamLength().longValue(), putObjectRequest.getMetadata().getContentLength());
    }

    /**
     * 创建对象访问url
     */
    @Test
    public void getObjectUrlTest() {
        String url = ossConvertImpl.getObjectUrl(BUCKET_NAME, PATH_KEY);
        String expectUrl = storeConfigProperties.getUrl();
        expectUrl = StringUtils.replace(expectUrl, REPLACE_BUCKET, BUCKET_NAME);
        expectUrl += PATH_KEY;
        Assert.assertEquals(expectUrl, url);
    }

    /**
     * 创建桶位
     *
     * @throws Exception
     */
    @Test
    public void createBucketTest() throws Exception {
        Result result = storeService.createBucket(null);
        Assert.assertEquals(ApiStatus.PARAM_ERROR.getCode(), result.getResultCode());
        result = storeService.createBucket(BUCKET_NAME);
        Assert.assertEquals("创建桶位失败", ApiStatus.SUCCESS.getCode(), result.getResultCode());
    }

    /**
     * 上传对象
     *
     * @throws Exception
     */
    @Test
    public void uploadObjectTest() throws Exception {
        ApiResult apiResult = storeService.uploadObject(storeRequest);
        Assert.assertEquals("上传对象失败", ApiStatus.SUCCESS.getCode(), apiResult.getResultCode());
    }
}
