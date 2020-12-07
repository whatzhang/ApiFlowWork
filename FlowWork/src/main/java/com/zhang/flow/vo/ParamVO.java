package com.zhang.flow.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author zhang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParamVO {
    @NotNull(message = "请求路径不能为空")
    @NotBlank(message = "请求路径不能为空")
    private String path;
    private Map<String, String> header;
}
