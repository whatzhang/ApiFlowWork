package com.zhang.flow.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.FillPatternType;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zhang
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@HeadStyle(fillPatternType = FillPatternType.SOLID_FOREGROUND, fillForegroundColor = 40)
public class FlowVO implements Serializable {
    @ExcelIgnore
    private String id;
    @ExcelProperty("顺序ID")
    private Long order;
    @ExcelProperty("请求URL")
    private String url;
    @ExcelProperty("请求方法(method)")
    private String method;
    @ExcelProperty("请求类型(ContentType)")
    private String reqContentType;
    @ExcelProperty("请求头")
    private Map<String, String> headerMap;
    @ExcelProperty("请求表单参数")
    private Map<String, String[]> paramMap;
    @ExcelProperty("请求参数")
    private String queryParam;
    @ExcelProperty("请求body")
    private String requestBody;
    @ExcelProperty("响应码")
    private String httpStatus;
    @ExcelProperty("响应类型(ContentType)")
    private String respContentType;
    @ExcelProperty("响应body")
    private String responseBody;
}
