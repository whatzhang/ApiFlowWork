package com.zhang.flow.controller;


import com.zhang.flow.service.FlowService;
import com.zhang.flow.vo.ParamVO;
import com.zhang.flow.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;


/**
 * @author zhang
 */
@Slf4j
@RestController
public class ManagerController {

    @Resource
    private FlowService flowService;

    @GetMapping("/list")
    public ResultVO list() {
        return flowService.list();
    }

    @PostMapping("/order")
    public ResultVO order(@RequestBody List<String> ids) {
        return flowService.order(ids);
    }

    @GetMapping("/start")
    public ResultVO start() {
        return flowService.start();
    }

    @GetMapping("/end")
    public ResultVO end() {
        return flowService.end();
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        flowService.export(response);
    }

    @PostMapping("/import")
    public ResultVO importFile(@RequestParam MultipartFile file) throws IOException {
       return  flowService.importFile(file);
    }

    @GetMapping("/execute")
    public ResultVO execute(@RequestBody @Valid ParamVO paramVO) {
        return flowService.execute(paramVO);
    }

}