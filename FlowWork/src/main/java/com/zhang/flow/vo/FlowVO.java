package com.zhang.flow.vo;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhang.flow.util.StringUtils;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@ColumnWidth(50)
@HeadStyle(fillPatternType = FillPatternType.SOLID_FOREGROUND, fillForegroundColor = 40)
public class FlowVO implements Serializable {
    @ExcelIgnore
    private String id;
    @ExcelProperty("顺序ID")
    private Long order;
    @ExcelProperty("请求URL")
    private String url;
    @ExcelProperty("请求URI")
    private String uri;
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
    @ExcelIgnore
    private Long time;

    public FlowVO(FlowExcelVO vo) {
        this.id = vo.getId();
        this.order = vo.getOrder();
        this.url = vo.getUrl();
        this.uri = vo.getUri();
        this.method = vo.getMethod();
        this.reqContentType = vo.getReqContentType();
        this.headerMap = StringUtils.isBlank(vo.getHeaderMap()) ? null : JSONUtil.toBean(vo.getHeaderMap(), Map.class);
        this.paramMap = StringUtils.isBlank(vo.getParamMap()) ? null : JSONUtil.toBean(vo.getParamMap(), Map.class);
        this.queryParam = vo.getQueryParam();
        this.requestBody = vo.getRequestBody();
        this.httpStatus = vo.getHttpStatus();
        this.respContentType = vo.getRespContentType();
        this.responseBody = vo.getResponseBody();
    }
}
