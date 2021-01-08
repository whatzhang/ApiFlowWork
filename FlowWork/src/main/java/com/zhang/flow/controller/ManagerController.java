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
@RequestMapping("/flowzhang")
public class ManagerController {

    @Resource
    private FlowService flowService;

    @DeleteMapping("/delete/{isOrder}")
    @ResponseBody
    public ResultVO delete(@PathVariable boolean isOrder) {
        return flowService.delete(isOrder);
    }

    @GetMapping("/list")
    @ResponseBody
    public ResultVO list() {
        return flowService.list();
    }

    @PostMapping("/order")
    @ResponseBody
    public ResultVO order(@RequestBody List<String> ids) {
        return flowService.order(ids);
    }

    @GetMapping("/start")
    @ResponseBody
    public ResultVO start() {
        return flowService.start();
    }

    @GetMapping("/end")
    @ResponseBody
    public ResultVO end() {
        return flowService.end();
    }

    @GetMapping("/export/{type}")
    @ResponseBody
    public void export(HttpServletResponse response, @PathVariable("type") String type) throws IOException {
        flowService.export(response, type);
    }

    @PostMapping("/import")
    @ResponseBody
    public ResultVO importFile(@RequestParam MultipartFile file) throws IOException {
        return flowService.importFile(file);
    }

    @PostMapping("/execute")
    @ResponseBody
    public ResultVO execute(@RequestBody @Valid ParamVO paramVO) {
        return flowService.execute(paramVO);
    }

}