package com.zhang.flow.cache;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zhang.flow.vo.FlowVO;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

/**
 * @author zhang
 */
public class FlowCache {
    public static Cache<String, FlowVO> FLOW_CACHE = Caffeine.newBuilder()
            .expireAfterWrite(1800, TimeUnit.MINUTES)
            .maximumSize(50)
            .build();

    public static ConcurrentSkipListMap<Long, FlowVO> ORDER_FLOW_CACHE = new ConcurrentSkipListMap<>();
}
