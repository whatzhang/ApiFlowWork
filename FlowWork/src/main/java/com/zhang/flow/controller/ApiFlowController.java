package com.zhang.flow.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhang.flow.config.ApiFlowPropertiesConfiguration;
import com.zhang.flow.service.FlowService;
import com.zhang.flow.vo.ParamVO;
import com.zhang.flow.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * @author zhang
 */
@Slf4j
@Controller
public class ApiFlowController {

    private final ApiFlowPropertiesConfiguration propertiesConfiguration;
    private final ObjectMapper objectMapper;

    @Resource
    private FlowService flowService;

    public ApiFlowController(ApiFlowPropertiesConfiguration config, ObjectMapper objectMapper) {
        this.propertiesConfiguration = config;
        this.objectMapper = objectMapper;
    }

    @GetMapping("${api-flow.mapping:/flowing}")
    public String playground(final Model model, final HttpServletRequest request) {
        model.addAttribute("pageTitle", propertiesConfiguration.getApiFlow().getPageTitle());
        model.addAttribute("properties", objectMapper.valueToTree(propertiesConfiguration.getApiFlow()));
        model.addAttribute("_csrf", request.getAttribute("_csrf"));
        return "flowing";
    }

    @DeleteMapping("${api-flow.mapping:/flowing}/delete/{isOrder}")
    @ResponseBody
    public ResultVO delete(@PathVariable boolean isOrder) {
        return flowService.delete(isOrder);
    }

    @GetMapping("${api-flow.mapping:/flowing}/list")
    @ResponseBody
    public ResultVO list() {
        return flowService.list();
    }

    @PostMapping("${api-flow.mapping:/flowing}/order")
    @ResponseBody
    public ResultVO order(@RequestBody List<String> ids) {
        return flowService.order(ids);
    }

    @GetMapping("${api-flow.mapping:/flowing}/start")
    @ResponseBody
    public ResultVO start() {
        return flowService.start();
    }

    @GetMapping("${api-flow.mapping:/flowing}/end")
    @ResponseBody
    public ResultVO end() {
        return flowService.end();
    }

    @GetMapping("${api-flow.mapping:/flowing}/export/{type}")
    @ResponseBody
    public void export(HttpServletResponse response, @PathVariable("type") String type) throws IOException {
        flowService.export(response, type);
    }

    @PostMapping("${api-flow.mapping:/flowing}/import")
    @ResponseBody
    public ResultVO importFile(@RequestParam MultipartFile file) throws IOException {
        return flowService.importFile(file);
    }

    @PostMapping("${api-flow.mapping:/flowing}/execute")
    @ResponseBody
    public ResultVO execute(@RequestBody ParamVO paramVO) {
        return flowService.execute(paramVO);
    }

}