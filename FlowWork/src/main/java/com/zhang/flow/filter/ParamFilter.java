package com.zhang.flow.filter;

import cn.hutool.core.util.IdUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.zhang.flow.vo.FlowVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Order(1)
@Component
public class ParamFilter implements Filter {
    @Qualifier("flowCache")
    @Autowired
    Cache<String, FlowVO> flowCache;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        log.info(httpServletRequest.getRequestURI());
        if (httpServletRequest.getRequestURI().contains("/flowzhang")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String type = httpServletRequest.getContentType();
        if (StringUtils.isNotBlank(type)) {
            if (type.contains(MediaType.MULTIPART_FORM_DATA_VALUE)
                    || type.contains(MediaType.APPLICATION_JSON_VALUE)
                    || type.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    || type.contains(MediaType.APPLICATION_XML_VALUE)
                    || type.contains(MediaType.TEXT_PLAIN_VALUE)
                    || type.contains(MediaType.TEXT_XML_VALUE)) {
                RequestWrapper reqWp = new RequestWrapper(httpServletRequest);
                ResponseWrapper respWp = new ResponseWrapper(httpServletResponse);
                filterChain.doFilter(reqWp, respWp);
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

    @Override
    public void destroy() {
    }
}
