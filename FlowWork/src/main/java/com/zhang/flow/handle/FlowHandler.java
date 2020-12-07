package com.zhang.flow.handle;


import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author zhang
 */
public class FlowHandler implements HandlerInterceptor {
    public static final String REGEX = "&";
    public static final String REGEX1 = "=";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //request
        Map<String, String> headers = new HashMap<>(70);
        Collections.list(request.getHeaderNames()).forEach(header -> headers.put(header.toLowerCase(Locale.ENGLISH), request.getHeader(header)));
        String uri = request.getRequestURI();
        String contentType = request.getContentType();
        String remoteAddr = request.getRemoteAddr();
        String method = request.getMethod();
        Map<String, String[]> paramMap = new HashMap<>(16);
        Map<String, String> queryMap = new HashMap<>(16);
        String bodyStr = null;

        if (HttpMethod.GET.toString().equals(method)) {
            String queryString = URLUtil.decode(request.getQueryString());
            queryMap = getQueryMap(queryString);
        } else {
            if (contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                BufferedReader bufferedReader = request.getReader();
                bodyStr = IoUtil.read(bufferedReader);
            } else if (contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                paramMap = request.getParameterMap();
            } else if (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                paramMap = request.getParameterMap();
            } else {
            }
        }
        //response


        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private Map<String, String> getQueryMap(String queryString) {
        Map<String, String> queryMap = new HashMap(16);
        if (StringUtils.isNotBlank(queryString)) {
            String[] arr = queryString.split(REGEX);
            for (String s : arr) {
                String[] param = s.split(REGEX1);
                if (param.length == 1) {
                    queryMap.put(param[0], null);
                } else if (param.length == 2) {
                    queryMap.put(param[0], param[1]);
                }
            }
        }
        return queryMap;
    }
}
