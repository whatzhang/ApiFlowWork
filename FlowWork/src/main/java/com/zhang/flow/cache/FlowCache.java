package com.zhang.flow.cache;


import com.zhang.flow.vo.FlowVO;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhang
 */
public class FlowCache {
    public static ConcurrentSkipListMap<String, FlowVO> ORDER_FLOW_CACHE = new ConcurrentSkipListMap<>();
    public static volatile Boolean IS_START = new Boolean(false);
}
