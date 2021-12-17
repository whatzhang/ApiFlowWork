package com.zhang.flow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

/**
 * @author zhang
 * @desc
 * @date 2021/12/17 16:53
 */
@Data
@ConfigurationProperties(prefix = "api")
@Validated
public class ApiFlowPropertiesConfiguration {
    @NestedConfigurationProperty
    private ApiFlowProperties apiFlow = new ApiFlowProperties();
}
