package com.zhang.flow.cache;


import com.zhang.flow.vo.FlowVO;

import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author zhang
 */
public class FlowCache {
    public static ConcurrentSkipListMap<String, FlowVO> ORDER_FLOW_CACHE = new ConcurrentSkipListMap<>();
    public static boolean IS_START = false;
}
