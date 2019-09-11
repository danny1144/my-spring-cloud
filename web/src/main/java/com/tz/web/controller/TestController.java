package com.tz.web.controller;

import com.tz.lock.Resubmit;
import com.tz.web.dto.RequestDTO;
import com.tz.web.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试类
 */
@RestController
@Slf4j
public class TestController {

    @RequestMapping("/test")
    @Resubmit(delaySeconds=10)
    public ResponseDTO testUrl(@RequestBody RequestDTO name){
        log.info(name.toString());
        return ResponseDTO.ok("data");
    }
}
