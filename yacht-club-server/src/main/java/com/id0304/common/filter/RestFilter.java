package com.id0304.common.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * @Author WuZhengHua
 * @Description TODO shiro跨域请求过滤器
 * @Date 2019/10/5 12:37
 */
@WebFilter(urlPatterns = "/*", filterName = "RestFilter")
public class RestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = null;
        if (request instanceof HttpServletRequest) {
            req = (HttpServletRequest) request;
        }

        HttpServletResponse res = null;
        if (response instanceof HttpServletResponse) {
            res = (HttpServletResponse) response;
        }
        if (req != null && res != null) {
            //设置允许传递的参数
            res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, Requestfrom");
            //设置允许带上cookie
            res.setHeader("Access-Control-Allow-Credentials", "true");
            String origin = Optional.ofNullable(req.getHeader("Origin")).orElse(req.getHeader("Referer"));
            //设置允许的请求来源
            res.setHeader("Access-Control-Allow-Origin",origin);
            //设置允许的请求方法
            res.setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
        }

        chain.doFilter(request, response);
    }
    @Override
    public void destroy() {
    }

}
