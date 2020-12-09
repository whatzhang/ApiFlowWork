package com.zhang.flow.service.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.zhang.flow.cache.FlowCache;
import com.zhang.flow.service.FlowService;
import com.zhang.flow.vo.FlowVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhang
 */
public class ImportDataListener extends AnalysisEventListener<FlowVO> {
    @Resource
    private FlowService flowService;

    @Override
    public void invoke(FlowVO flowVO, AnalysisContext analysisContext) {
        FlowCache.ORDER_FLOW_CACHE.put(flowVO.getOrder(), flowVO);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        flowService.sortFlow();
    }
}
