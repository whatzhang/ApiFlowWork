package com.zhang.flow.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zhang.flow.vo.FlowVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author zhang
 */
@Configuration
public class FlowConfig {
    @Bean(value = "flowCache")
    public Cache<String, FlowVO> flowCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(18000, TimeUnit.MINUTES)
                .initialCapacity(1000)
                .maximumSize(50)
                .build();
    }
}
