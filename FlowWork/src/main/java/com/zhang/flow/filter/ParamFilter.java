package com.zhang.flow.filter;

import cn.hutool.core.util.IdUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.zhang.flow.cache.FlowCache;
import com.zhang.flow.config.Constant;
import com.zhang.flow.util.StringUtils;
import com.zhang.flow.vo.FlowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhang
 */
@Slf4j
public class ParamFilter implements Filter {

    @Resource
    private Cache<String, FlowVO> flowCache;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!FlowCache.IS_START) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        log.info(httpServletRequest.getRequestURI());
        if (httpServletRequest.getRequestURI().contains(Constant.FLOW_URI_PREF)
                || httpServletRequest.getRequestURI().equals("/")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String type = httpServletRequest.getContentType();
        boolean reqFlag = isReqFlag(type);
        if (reqFlag || StringUtils.isBlank(type)) {
            RequestWrapper reqWp = new RequestWrapper(httpServletRequest);
            ResponseWrapper respWp = new ResponseWrapper(httpServletResponse);
            filterChain.doFilter(reqWp, respWp);
            String respType = respWp.getContentType();
            boolean respFlag = isReqFlag(respType);
            if (respFlag || StringUtils.isBlank(respType)) {
                String uuid = IdUtil.simpleUUID();
                FlowVO flow = FlowVO.builder()
                        .id(uuid)
                        .method(reqWp.getMethod())
                        .url(reqWp.getRequestURL().toString())
                        .uri(reqWp.getRequestURI())
                        .reqContentType(reqWp.getContentType())
                        .headerMap(reqWp.getHeaderMap())
                        .paramMap(reqWp.getParamMap())
                        .queryParam(reqWp.getQueryParam())
                        .requestBody(reqWp.getRequestBody())
                        .respContentType(respWp.getContentType())
                        .httpStatus(String.valueOf(respWp.getStatus()))
                        .responseBody(respWp.getResponseBody())
                        .time(System.nanoTime())
                        .build();
                ServletOutputStream os = servletResponse.getOutputStream();
                os.write(respWp.getResponseBodyByte());
                os.flush();
                os.close();
                flowCache.put(uuid, flow);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean isReqFlag(String type) {
        return StringUtils.isNotBlank(type) && (type.contains(MediaType.MULTIPART_FORM_DATA_VALUE)
                || type.contains(MediaType.APPLICATION_JSON_VALUE)
                || type.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                || type.contains(MediaType.APPLICATION_XML_VALUE)
                || type.contains(MediaType.TEXT_PLAIN_VALUE)
                || type.contains(MediaType.TEXT_XML_VALUE));
    }

    @Override
    public void destroy() {
    }
}
