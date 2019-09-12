package com.tz.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tz.lock.Resubmit;
import com.tz.lock.ResubmitLock;
import com.tz.web.dto.RequestDTO;
import com.tz.web.dto.ResponseDTO;
import com.tz.web.utils.JsonUtils;
import lombok.extern.log4j.Log4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Description 数据重复提交校验
 **/
@Log4j
@Aspect
@Component
public class ResubmitDataAspect {

    private final static Object PRESENT = new Object();

    @Around("@annotation(com.tz.lock.Resubmit)")
    public Object handleResubmit(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        //获取注解信息
        Resubmit annotation = method.getAnnotation(Resubmit.class);
        int delaySeconds = annotation.delaySeconds();
        Object[] pointArgs = joinPoint.getArgs();
        String key = "";
        //获取第一个参数
        Object firstParam = pointArgs[0];
        //解析参数
         if (firstParam != null) {
            String s = JsonUtils.objectToJson(firstParam);
            //生成加密参数 使用了content_MD5的加密方式
            key = ResubmitLock.handleKey(s);
        }
        //执行锁
        boolean lock = false;
        try {
            //设置解锁key
            lock = ResubmitLock.getInstance().lock(key, PRESENT);
            if (lock) {
                //放行
                return joinPoint.proceed();
            } else {
                //响应重复提交异常
                return new ResponseDTO("error");
            }
        } finally {
            //设置解锁key和解锁时间
            ResubmitLock.getInstance().unLock(lock, key, delaySeconds);
        }
    }
}
