package com.igen.store.convert;

import com.igen.store.domain.StoreRequest;

/**
 * Desc: 存储转换校验接口
 * User: wangmin
 * Date: 2017/12/5
 * Time: 下午3:40
 */
public interface IConvert<T> {

    /**
     * 对象转换
     *
     * @param storeRequest
     * @return
     */
    T convert(StoreRequest storeRequest) throws Exception;

    /**
     * 参数校验
     *
     * @param storeRequest
     * @return
     */
    String validate(StoreRequest storeRequest);

    /**
     * 获得可访问的url
     * @param bucketName
     * @param pathKey
     * @return
     */
    String getObjectUrl(String bucketName,String pathKey);

    /**
     * 替换bucket占位符
     */
    String REPLACE_BUCKET = "replace-bucket";
}
