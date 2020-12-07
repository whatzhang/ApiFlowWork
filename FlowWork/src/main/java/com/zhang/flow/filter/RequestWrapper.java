package com.zhang.flow.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author zhang
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    private String requestBody;
    private Map<String, String[]> paramMap = new HashMap<>(16);
    private String queryParam;
    private Map<String, String> headerMap = new HashMap<>(16);

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        String method = request.getMethod();
        String contentType = request.getContentType();
        Collections.list(request.getHeaderNames()).forEach(header -> headerMap.put(header.toLowerCase(Locale.ENGLISH), request.getHeader(header)));
        if (HttpMethod.GET.toString().equals(method)) {
            queryParam = URLUtil.decode(request.getQueryString());
        } else {
            if (contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                BufferedReader bufferedReader = request.getReader();
                requestBody = IoUtil.read(bufferedReader);
            } else if (contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                paramMap = request.getParameterMap();
            } else if (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                paramMap = request.getParameterMap();
            } else {
            }
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody.getBytes());
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    private Map<String, String> getQueryMap(String queryString) {
        Map<String, String> queryMap = new HashMap(16);
        if (StringUtils.isNotBlank(queryString)) {
            String[] arr = queryString.split("&");
            for (String s : arr) {
                String[] param = s.split("=");
                if (param.length == 1) {
                    queryMap.put(param[0], null);
                } else if (param.length == 2) {
                    queryMap.put(param[0], param[1]);
                }
            }
        }
        return queryMap;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public Map<String, String[]> getParamMap() {
        return paramMap;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public String getQueryParam() {
        return queryParam;
    }
}