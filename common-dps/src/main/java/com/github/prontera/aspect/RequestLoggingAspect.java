package com.github.prontera.aspect;

import com.github.prontera.RequestLogging;
import com.github.prontera.config.RequestAttributeConst;
import com.github.prontera.web.RequestDetailsLogger;
import com.github.prontera.web.ResponseDetailsLogger;
import com.github.prontera.web.ServletContextHolder;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;

/**
 * 本类设计为当有被@RequestBodyLogs修饰的@ControllerAdvice或者@Controller抛出异常时记录输入输出,
 * 其他情况仅记录被标记的@RequestMapping或@ResponseBody方法
 *
 * @author Zhao Junjian
 * @see RequestLogging
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 */
@Aspect
public class RequestLoggingAspect implements Ordered {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingAspect.class);
    private final int order;

    public RequestLoggingAspect(int order) {
        this.order = order;
    }

    @Around(value = "within(com.github.prontera..*) " +
            "&& (@annotation(org.springframework.web.bind.annotation.ResponseBody)" +
            "|| @annotation(org.springframework.web.bind.annotation.RequestMapping)) " +
            "&& @annotation(com.github.prontera.RequestLogging)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // 生成请求日志
        RequestDetailsLogger requestLog = generateJsonRequestDetails();
        // 获取Swagger上的API描述
        injectApiOperationDescription(joinPoint, requestLog);
        // 执行真实请求
        final Object proceed = joinPoint.proceed();
        // 当响应完成时, 打印完整的'request & response'信息
        requestLog.setResponseTime(OffsetDateTime.now());
        LOGGER.debug("RequestLoggingAspect#\r\nREQUEST->\r\n{}\r\nRESPONSE->\r\n {}", requestLog, ResponseDetailsLogger.with(proceed));
        // 放行
        return proceed;
    }

    /**
     * 创建通用的日志输出模式并绑定线程
     *
     * @return 日志模型
     */
    private RequestDetailsLogger generateJsonRequestDetails() {
        RequestDetailsLogger logDetails = (RequestDetailsLogger) ServletContextHolder.getRequest().getAttribute(RequestAttributeConst.DETAILS_KEY);
        if (logDetails == null) {
            logDetails = new RequestDetailsLogger();
            ServletContextHolder.getRequest().setAttribute(RequestAttributeConst.DETAILS_KEY, logDetails);
        }
        return logDetails;
    }

    private void injectApiOperationDescription(ProceedingJoinPoint joinPoint, RequestDetailsLogger logDetails) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        final ApiOperation operate = method.getAnnotation(ApiOperation.class);
        if (operate != null) {
            logDetails.setApiDesc(operate.value());
        }
    }

    @Override
    public int getOrder() {
        return order;
    }
}
