package com.litserver.global.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class XSSRequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
//        String accountId = CookieUtil.getValue((HttpServletRequest) request, BrokerCoreConstants.ACCOUNT_ID_COOKIE_NAME);
//        if (!Strings.isNullOrEmpty(accountId)) {
//            MDC.put(LogBizConstants.ACCOUNT_ID, accountId);
//        }
        chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
    }

}
