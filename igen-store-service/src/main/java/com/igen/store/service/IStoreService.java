package com.igen.store.service;

import com.igen.bean.ApiResult;
import com.igen.bean.Result;
import com.igen.store.domain.StoreRequest;

/**
 * Desc: 存储服务接口
 * User: wangmin
 * Date: 2017/12/4
 * Time: 上午10:46
 */
public interface IStoreService {

    /**
     * 初始化
     */
    void init();

    /**
     * 资源销毁
     */
    void destroy();

    /**
     * 创建桶位
     *
     * @param bucketName
     * @return
     */
    Result createBucket(String bucketName) throws Exception;

    /**
     * 上传文件对象
     *
     * @param storeRequest
     * @return
     */
    ApiResult uploadObject(StoreRequest storeRequest) throws Exception;

    String URL = "url";

}
