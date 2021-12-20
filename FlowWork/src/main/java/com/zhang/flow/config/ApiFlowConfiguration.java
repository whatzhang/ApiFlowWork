package com.zhang.flow.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zhang.flow.controller.ApiFlowController;
import com.zhang.flow.filter.ParamFilter;
import com.zhang.flow.vo.FlowVO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.concurrent.TimeUnit;

/**
 * @author zhang
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({DispatcherServlet.class})
@EnableConfigurationProperties({ApiFlowPropertiesConfiguration.class})
public class ApiFlowConfiguration {
    @Primary
    @Bean
    @ConditionalOnProperty(value = "api-flow.enabled", havingValue = "true", matchIfMissing = true)
    ApiFlowController playgroundController(ApiFlowPropertiesConfiguration apiFlowPropertiesConfiguration, final ObjectMapper objectMapper) {
        return new ApiFlowController(apiFlowPropertiesConfiguration, objectMapper);
    }

    @Bean(value = "flowCache")
    @ConditionalOnProperty(value = "api-flow.enabled", havingValue = "true", matchIfMissing = true)
    Cache<String, FlowVO> flowCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(18000, TimeUnit.MINUTES)
                .initialCapacity(1000)
                .maximumSize(50)
                .build();
    }

    @Bean
    @Order(1)
    @ConditionalOnProperty(value = "api-flow.enabled", havingValue = "true", matchIfMissing = true)
    ParamFilter paramFilter() {
        return new ParamFilter();
    }
}
