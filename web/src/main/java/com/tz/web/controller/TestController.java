package com.tz.web.controller;

import com.tz.lock.CacheLock;
import com.tz.lock.CacheParam;
import com.tz.lock.Resubmit;
import com.tz.web.dto.RequestDTO;
import com.tz.web.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 测试类
 */
@RestController
@Slf4j
public class TestController {

    @RequestMapping("/test")
    @Resubmit(delaySeconds=5)
    public ResponseDTO testUrl(@RequestBody RequestDTO name){
        log.info(name.toString());
        return ResponseDTO.ok("data");
    }

    @CacheLock(prefix = "books",expire = 5)
    @GetMapping
    public String query(@CacheParam(name = "token") @RequestParam String token) {
        return "success - " + token;
    }
}
