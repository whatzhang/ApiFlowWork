package com.zhang.flow.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Collections;
import java.util.Map;

/**
 * @author zhang
 * @desc
 * @date 2021/12/17 16:55
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiFlowProperties {

    private String endpoint = "/flowing";

    @JsonIgnore
    private String pageTitle = "api流测试";

    private Map<String, String> headers = Collections.emptyMap();
}
