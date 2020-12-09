package com.zhang.flow.service.impl;

import cn.hutool.http.ContentType;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.github.benmanes.caffeine.cache.Cache;
import com.zhang.flow.cache.FlowCache;
import com.zhang.flow.service.FlowService;
import com.zhang.flow.vo.FlowVO;
import com.zhang.flow.vo.ParamVO;
import com.zhang.flow.vo.ResultVO;
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
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zhang
 */
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
        return new ResultVO("200", "success", flowCache.asMap().values());
    }

    @Override
    public ResultVO order(List<String> ids) {
        Assert.notEmpty(ids, "接口id不能为空");
        for (int i = 0; i < ids.size(); i++) {
            FlowVO vo = flowCache.getIfPresent(ids.get(i));
            if (Objects.nonNull(vo)) {
                FlowCache.ORDER_FLOW_CACHE.put(Long.valueOf(i), vo);
            }
        }
        return new ResultVO("200", "success", FlowCache.ORDER_FLOW_CACHE.values());
    }

    @Override
    public ResultVO start() {
        flowCache.cleanUp();
        FlowCache.ORDER_FLOW_CACHE.clear();
        return new ResultVO("200", "success", null);
    }

    @Override
    public ResultVO end() {
        FlowCache.ORDER_FLOW_CACHE.clear();
        return new ResultVO("200", "success", null);
    }

    @Override
    public void export(HttpServletResponse response, String type) throws IOException {
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''apiFlow.xlsx");
            List<FlowVO> list = new ArrayList<>();
            if ("1".equals(type)) {
                flowCache.asMap().values().forEach(o -> {
                    list.add(o);
                });
            } else {
                FlowCache.ORDER_FLOW_CACHE.values().forEach(o -> {
                    list.add(o);
                });
            }
            EasyExcel.write(response.getOutputStream(), FlowVO.class).excelType(ExcelTypeEnum.XLSX).sheet(0, "api数据").doWrite(list);
        } catch (Exception e) {
            e.printStackTrace();
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().println(new ResultVO("500", "下载文件失败", null));
        }
    }

    @Override
    public ResultVO importFile(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), FlowVO.class, new ImportDataListener()).sheet().headRowNumber(1).doRead();
        return new ResultVO("200", "success", null);
    }

    @Override
    public ResultVO execute(ParamVO paramVO) {
        if (FlowCache.ORDER_FLOW_CACHE.isEmpty()) {
            return new ResultVO("500", "可执行的列表为空！", null);
        }
        List<FlowVO> list = new ArrayList<>();
        try {
            for (FlowVO vo : FlowCache.ORDER_FLOW_CACHE.values()) {
                Request request = new Request.Builder()
                        .url(paramVO.getPath() + vo.getUrl() + (StringUtils.isNotBlank(vo.getQueryParam()) ? vo.getQueryParam() : ""))
                        .method(vo.getMethod(), returnRequestBody(vo))
                        .headers(Headers.of(vo.getHeaderMap()))
                        .build();
                Response response = CLIENT.newCall(request).execute();
                vo.setRespContentType(response.body().contentType().toString());
                vo.setResponseBody(response.body().string());
                vo.setHttpStatus(String.valueOf(response.code()));
                list.add(vo);
            }
        } catch (Exception e) {
            return new ResultVO("500", "failed", list);
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

    @Override
    public void sortFlow() {
        ConcurrentSkipListMap<Long, FlowVO> map = new ConcurrentSkipListMap<>();
        FlowCache.ORDER_FLOW_CACHE.entrySet().stream()
                .sorted(Map.Entry.<Long, FlowVO>comparingByKey()
                        .reversed()).forEachOrdered(e -> map.put(e.getKey(), e.getValue()));
        FlowCache.ORDER_FLOW_CACHE = map;
    }
}