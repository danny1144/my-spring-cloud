package com.tz.web.controller;

import com.netflix.appinfo.InstanceInfo;
import com.tz.lock.CacheLock;
import com.tz.lock.CacheParam;
import com.tz.lock.Resubmit;
import com.tz.web.dto.RequestDTO;
import com.tz.web.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping(value = "/router", method = RequestMethod.GET)
    @ResponseBody
    public String router() {
        // 查找服务列表
        List<ServiceInstance> ins = getServiceInstances();
        // 输出服务信息及状态
        for (ServiceInstance service : ins) {
            EurekaDiscoveryClient.EurekaServiceInstance esi = (EurekaDiscoveryClient.EurekaServiceInstance) service;
            InstanceInfo info = esi.getInstanceInfo();
            System.out.println(info.getAppName() + "---" + info.getInstanceId()
                    + "---" + info.getStatus());
        }
        return "";
    }

    /**
     * 查询可用服务
     */
    private List<ServiceInstance> getServiceInstances() {
        List<String> ids = discoveryClient.getServices();
        List<ServiceInstance> result = new ArrayList<ServiceInstance>();
        for (String id : ids) {
            List<ServiceInstance> ins = discoveryClient.getInstances(id);
            result.addAll(ins);
        }
        return result;
    }


}
