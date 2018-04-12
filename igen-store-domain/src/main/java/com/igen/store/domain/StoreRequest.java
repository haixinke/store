package com.igen.store.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.InputStream;

/**
 * Desc: 存储请求对象
 * User: wangmin
 * Date: 2017/12/4
 * Time: 上午10:22
 */
@Data
@Accessors(chain = true)
public class StoreRequest {

    /**
     * 桶位名称
     */
    private String bucketName;

    /**
     * 桶位内部的文件路径key
     */
    private String pathKey;

    /**
     * 文件输入流
     */
    private InputStream inputStream;

    /**
     * 输入流的长度
     */
    private Long streamLength;

    /**
     * 文件名
     */
    private String fileName;

}
