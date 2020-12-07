package com.zhang.flow.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.zhang.flow.cache.FlowCache;
import com.zhang.flow.vo.FlowVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        RequestWrapper reqWp = new RequestWrapper(httpServletRequest);
        ResponseWrapper respWp = new ResponseWrapper(httpServletResponse);
        filterChain.doFilter(reqWp, respWp);
        String uuid = IdUtil.simpleUUID();
        FlowVO flow = FlowVO.builder()
                .id(uuid)
                .method(reqWp.getMethod())
                .url(reqWp.getRequestURL().toString())
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
        FlowCache.FLOW_CACHE.put(uuid, flow);
    }

    @Override
    public void destroy() {
    }
}
