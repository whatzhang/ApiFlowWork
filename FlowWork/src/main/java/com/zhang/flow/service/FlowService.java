package com.zhang.flow.service;

import com.zhang.flow.vo.ParamVO;
import com.zhang.flow.vo.ResultVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author zhang
 */
public interface FlowService {
    ResultVO list();

    ResultVO order(List<String> ids);

    ResultVO start();

    ResultVO end();

    void export(HttpServletResponse response, String type) throws IOException;

    ResultVO execute(ParamVO paramVO);

    ResultVO importFile(MultipartFile file) throws IOException;

    void sortFlow();
}
