package com.igen.store.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Desc: 存储aop（记录log标识，打印执行时间）
 * User: wangmin
 * Date: 2017/12/15
 * Time: 下午4:02
 */
@Slf4j
@Aspect
@Component
public class StoreAop {

    private static final String APP_NAME = "igen-store-api";

    private static final String LOGID = "logId";

    @Around(value = "execution(public * com.igen.store.controller..*.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature joinPointObject = (MethodSignature) point.getSignature();
        String methodName = joinPointObject.getName();
        String logId = APP_NAME + UUID.randomUUID();
        ThreadContext.put(LOGID, logId);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = point.proceed();
        ThreadContext.remove(LOGID);
        stopWatch.stop();
        log.info("logId:{} method:{} execute {} milliseconds", logId, methodName, stopWatch.getTime());
        return result;
    }
}
