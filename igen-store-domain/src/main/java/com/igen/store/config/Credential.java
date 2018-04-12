package com.igen.store.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Desc:
 * User: wangmin
 * Date: 2017/12/4
 * Time: 上午10:16
 */
@Data
@Accessors(chain = true)
public class Credential {

    private String appId;

    @NotBlank
    private String secrectId;

    @NotBlank
    private String secrectKey;
}
