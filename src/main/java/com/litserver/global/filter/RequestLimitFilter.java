package com.litserver.global.filter;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.AtomicLongMap;
import com.litserver.global.util.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Slf4j
public class RequestLimitFilter implements Filter {

    private static final ImmutableMap<String, String> NOTICE_MAP = ImmutableMap.<String, String>builder()
//            .put(Locale.CHINA.toString(), JsonUtil.defaultGson().toJson(new ErrorRet(30012, "网络拥挤，请稍后重试")))
//            .put(Locale.US.toString(), JsonUtil.defaultGson().toJson(new ErrorRet(30012, "Network busy, Try again later")))
            .build();

    private static final AtomicLongMap<String> REQUEST_PATH_COUNTER = AtomicLongMap.create();

    private Integer singlePathMaxThreads;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        singlePathMaxThreads = Ints.tryParse(filterConfig.getInitParameter("maxThreads"));
        if (singlePathMaxThreads == null) {
            singlePathMaxThreads = 150;
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestPath = request.getRequestURI();
        long currentThreads = REQUEST_PATH_COUNTER.incrementAndGet(requestPath);
        if (currentThreads > singlePathMaxThreads) {
            log.info("requestPath:\"{}\" requestIp:\"{}\" workThread:{}", requestPath, IPUtils.getIpAddr(request), currentThreads);
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.setStatus(400);
            String message = NOTICE_MAP.getOrDefault(LocaleContextHolder.getLocale().toString(),
                    NOTICE_MAP.get(Locale.US.toString()));
            response.getWriter().write(message);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            REQUEST_PATH_COUNTER.decrementAndGet(requestPath);
        }
    }
}
