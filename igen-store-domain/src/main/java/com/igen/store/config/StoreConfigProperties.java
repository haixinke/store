package com.igen.store.config;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

/**
 * Desc: 存储配置
 * User: wangmin
 * Date: 2017/11/30
 * Time: 下午2:28
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "store")
public class StoreConfigProperties {

    /**
     * 认证信息
     */
    private Credential credential;

    /**
     * 区域
     */
    @NotBlank
    private String region;

    /**
     * 平台（腾讯：cos、阿里oss）
     */
    @NotBlank
    private String platform;

    /**
     * 是否cdn加速
     */
    @NotNull
    private Boolean cdn;

    /**
     * 默认桶位
     */
    @NotBlank
    private String defaultBucket;

    /**
     * 上传对象的节点（oss）
     */
    private String endpoint;

    /**
     * 默认访问地址
     */
    @NotBlank
    private String url;

    /**
     * cdn访问地址（cos）
     */
    private String cdnUrl;
}
