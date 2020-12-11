package com.zhang.flow.service.impl;

import cn.hutool.http.ContentType;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.github.benmanes.caffeine.cache.Cache;
import com.zhang.flow.cache.FlowCache;
import com.zhang.flow.service.FlowService;
import com.zhang.flow.vo.FlowExcelVO;
import com.zhang.flow.vo.FlowVO;
import com.zhang.flow.vo.ParamVO;
import com.zhang.flow.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhang
 */
@Slf4j
@Service
public class FlowServiceImpl implements FlowService {

    private static OkHttpClient CLIENT = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    @Qualifier("flowCache")
    @Autowired
    Cache<String, FlowVO> flowCache;

    @Override
    public ResultVO list() {
        return new ResultVO("200", "success",
                flowCache.asMap().values().stream().sorted(Comparator.comparing(c -> c.getTime())).collect(Collectors.toList()));
    }

    @Override
    public ResultVO order(List<String> ids) {
        Assert.notEmpty(ids, "接口id不能为空");
        FlowCache.ORDER_FLOW_CACHE.clear();
        for (int i = 0; i < ids.size(); i++) {
            FlowVO vo = flowCache.getIfPresent(ids.get(i));
            if (Objects.nonNull(vo)) {
                FlowCache.ORDER_FLOW_CACHE.put(vo.getId(), vo);
            }
        }
        return new ResultVO("200", "success", FlowCache.ORDER_FLOW_CACHE.values());
    }

    @Override
    public ResultVO delete(boolean isOrder) {
        if (isOrder) {
            FlowCache.ORDER_FLOW_CACHE.clear();
        } else {
            flowCache.invalidateAll();
        }
        return new ResultVO("200", "success", null);
    }

    @Override
    public ResultVO start() {
        FlowCache.IS_START = true;
        FlowCache.ORDER_FLOW_CACHE.clear();
        flowCache.invalidateAll();
        return new ResultVO("200", "success", null);
    }

    @Override
    public ResultVO end() {
        FlowCache.IS_START = false;
        FlowCache.ORDER_FLOW_CACHE.clear();
        flowCache.invalidateAll();
        return new ResultVO("200", "success", null);
    }

    @Override
    public void export(HttpServletResponse response, String type) throws IOException {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''apiFlow.xlsx");
            List<FlowExcelVO> list = new ArrayList<>();
            if ("1".equals(type)) {
                flowCache.asMap().values().forEach(o -> {
                    list.add(new FlowExcelVO(o));
                });
            } else {
                FlowCache.ORDER_FLOW_CACHE.values().forEach(o -> {
                    list.add(new FlowExcelVO(o));
                });
            }
            EasyExcel.write(response.getOutputStream(), FlowExcelVO.class).excelType(ExcelTypeEnum.XLSX).sheet(0, "api数据").doWrite(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultVO importFile(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), FlowExcelVO.class, new ImportDataListener()).sheet().headRowNumber(1).doRead();
        return new ResultVO("200", "success", null);
    }

    @Override
    public ResultVO execute(ParamVO paramVO) {
        if (FlowCache.ORDER_FLOW_CACHE.isEmpty()) {
            return new ResultVO("500", "可执行的列表为空！", null);
        }
        List<FlowVO> list = new ArrayList<>();
        for (FlowVO vo : FlowCache.ORDER_FLOW_CACHE.values()) {
            if (!paramVO.getIds().contains(vo.getId())) {
                continue;
            }
            Response response = null;
            try {
                String path = StringUtils.isBlank(paramVO.getPath()) ? vo.getUrl() : (paramVO.getPath() + vo.getUri());
                Request request = new Request.Builder()
                        .url(path + (StringUtils.isNotBlank(vo.getQueryParam()) ? vo.getQueryParam() : ""))
                        .method(vo.getMethod(), returnRequestBody(vo))
                        .headers(Headers.of(vo.getHeaderMap()))
                        .build();
                response = CLIENT.newCall(request).execute();
                vo.setRespContentType(response.body().contentType().toString());
                vo.setResponseBody(response.body().string());
                vo.setHttpStatus(String.valueOf(response.code()));
            } catch (Exception e) {
                log.warn("ID=[{}],URL=[{}],execute error", vo.getId(), vo.getUrl());
            } finally {
                response.close();
            }
            list.add(vo);
        }
        return new ResultVO("200", "success", list);
    }

    private RequestBody returnRequestBody(FlowVO vo) {
        if (StringUtils.isBlank(vo.getReqContentType())) {
            return null;
        }
        if (vo.getReqContentType().contains(ContentType.FORM_URLENCODED.getValue())) {
            FormBody.Builder bd = new FormBody.Builder();
            Map<String, String[]> map = vo.getParamMap();
            if (Objects.nonNull(map) && !map.isEmpty()) {
                map.forEach((k, v) -> {
                    Arrays.stream(v).forEach(o -> {
                        bd.add(k, o);
                    });
                });
            }
            FormBody formBody = bd.build();
            return formBody;
        }
        if (vo.getReqContentType().contains(ContentType.MULTIPART.getValue())) {
            vo.getParamMap();
            MultipartBody.Builder bd = new MultipartBody.Builder();
            Map<String, String[]> map = vo.getParamMap();
            if (Objects.nonNull(map) && !map.isEmpty()) {
                map.forEach((k, v) -> {
                    Arrays.stream(v).forEach(o -> {
                        bd.addFormDataPart(k, o);
                    });
                });
            }
            bd.setType(MediaType.parse(ContentType.MULTIPART.getValue()));
            RequestBody body = bd.build();
            return body;
        }
        if (vo.getReqContentType().contains(ContentType.JSON.getValue())) {
            return RequestBody.create(MediaType.parse(vo.getReqContentType()), vo.getRequestBody());
        }
        if (vo.getReqContentType().contains(ContentType.TEXT_XML.getValue())) {
            return RequestBody.create(MediaType.parse(vo.getReqContentType()), vo.getRequestBody());
        }
        if (vo.getReqContentType().contains(ContentType.TEXT_PLAIN.getValue())) {
            return RequestBody.create(MediaType.parse(vo.getReqContentType()), vo.getRequestBody());
        }
        return null;
    }
}