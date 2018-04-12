package com.igen.store.controller;

import com.igen.bean.ApiResult;
import com.igen.bean.Result;
import com.igen.constant.Constants;
import com.igen.constant.EnumConstans.ApiStatus;
import com.igen.store.BaseTest;
import com.igen.utils.JsonUtils;
import com.igen.utils.ObjectStoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.WebApplicationContext;

/**
 * Desc: 存储controller测试
 * User: wangmin
 * Date: 2017/12/4
 * Time: 下午2:52
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreControllerTests extends BaseTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * 创建桶位测试
     *
     * @throws Exception
     */
    @Test
    public void createBucket() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/createBucket?bucketName=" + BUCKET_NAME).accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        Assert.assertEquals(HttpStatus.SC_OK, mvcResult.getResponse().getStatus());
        String resultString = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue(StringUtils.isNotBlank(resultString));
        Result result = JsonUtils.fromJson(resultString, Result.class);
        Assert.assertEquals(ApiStatus.SUCCESS.getCode(), result.getResultCode());
    }

    /**
     * 上传文件测试（server）
     *
     * @throws Exception
     */
    @Test
    public void uploadObjectTest() throws Exception {
        Resource resource = new ClassPathResource(FILE_NAME);
        MockMultipartFile file = new MockMultipartFile(Constants.FILE, FILE_NAME, null, FileCopyUtils.copyToByteArray(resource.getInputStream()));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/uploadObject")
                .file(file)
                .param(Constants.BUCKET_NAME, BUCKET_NAME)
                .param(Constants.PATH_KEY, PATH_KEY)
                .accept(MediaType.APPLICATION_JSON_UTF8)).andReturn();
        Assert.assertEquals(HttpStatus.SC_OK, mvcResult.getResponse().getStatus());
        String resultString = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue(StringUtils.isNotBlank(resultString));
        ApiResult result = JsonUtils.fromJson(resultString, ApiResult.class);
        Assert.assertEquals(ApiStatus.SUCCESS.getCode(), result.getResultCode());
    }

    /**
     * 上传文件测试（client）
     *
     * @throws Exception
     */
    @Test
    public void uploadObjectClientTest() throws Exception {
        Resource resource = new ClassPathResource("test.jpg");
        String url = "http://139.199.94.44:8090/igen-store/uploadObject";
        String pathKey = "image/test/";
        ApiResult apiResult = ObjectStoreUtils.uploadObject(url, "igen", pathKey,"test.jpg", resource.getInputStream());
        Assert.assertEquals("000", apiResult.getResultCode());
    }

}
