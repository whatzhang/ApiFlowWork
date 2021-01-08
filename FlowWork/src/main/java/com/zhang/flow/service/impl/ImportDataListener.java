package com.zhang.flow.service.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.github.benmanes.caffeine.cache.Cache;
import com.zhang.flow.cache.FlowCache;
import com.zhang.flow.service.FlowService;
import com.zhang.flow.vo.FlowExcelVO;
import com.zhang.flow.vo.FlowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;

/**
 * @author zhang
 */
public class ImportDataListener extends AnalysisEventListener<FlowExcelVO> {
    @Qualifier("flowCache")
    @Autowired
    Cache<String, FlowVO> flowCache;
    @Resource
    private FlowService flowService;

    @Override
    public void invoke(FlowExcelVO flowVO, AnalysisContext analysisContext) {
        FlowCache.ORDER_FLOW_CACHE.clear();
        flowCache.invalidateAll();
        FlowCache.ORDER_FLOW_CACHE.put(flowVO.getId(), new FlowVO(flowVO));
        flowCache.put(flowVO.getId(), new FlowVO(flowVO));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
