package com.zhang.flow.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * @author zhang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParamVO {
    @NotEmpty
    private List<String> ids;
    private String path;
    private Map<String, String> header;
}
