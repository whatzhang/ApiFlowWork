package com.zhang.flow.cache;


import com.zhang.flow.vo.FlowVO;

import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author zhang
 */
public class FlowCache {
    public static ConcurrentSkipListMap<Long, FlowVO> ORDER_FLOW_CACHE = new ConcurrentSkipListMap<>();
}
