package com.co1119.kanban.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingAspect {

    @Around("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    public Object logAroundControllers(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
        HttpServletResponse response = attrs != null ? attrs.getResponse() : null;

        long start = System.currentTimeMillis();
        try {
            if (request != null) {
                String method = request.getMethod();
                String uri = request.getRequestURI();
                String query = request.getQueryString();
                String remote = request.getRemoteAddr();
                log.info("Incoming request: {} {}{} from {}", method, uri, (query != null ? "?" + query : ""), remote);
            } else {
                log.info("Incoming request: (no HttpServletRequest available) - proceeding");
            }

            Object result = pjp.proceed();

            long elapsed = System.currentTimeMillis() - start;
            if (request != null && response != null) {
                log.info("Handled request: {} {} => status={} time={}ms",
                        request.getMethod(), request.getRequestURI(), response.getStatus(), elapsed);
            } else {
                log.info("Handled request: (no servlet attrs) time={}ms", elapsed);
            }

            return result;
        } catch (Throwable t) {
            long elapsed = System.currentTimeMillis() - start;
            if (request != null) {
                log.warn("Exception handling request: {} {} after {}ms", request.getMethod(), request.getRequestURI(),
                        elapsed, t);
            } else {
                log.warn("Exception handling request after {}ms", elapsed, t);
            }
            throw t;
        }
    }
}
