package com.id0304.common.shiro;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author WuZhengHua
 * @Description TODO shiro重写登录认证过滤器
 * @Date 2019/10/5 12:25
 */
@Slf4j
public class MyAuthenticationFilter extends FormAuthenticationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return super.isAccessAllowed(request, response, mappedValue);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 这个过滤器默认跳转到定制的登录页,前后端分离项目中改为返回一个未登录的json信息
     * @Date 12:32 2019/10/5
     **/
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        Subject subject = SecurityUtils.getSubject();
        Object user = subject.getPrincipal();
        if (Objects.equals(user, null)) {
            log.error("#####未进行登录认证");
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 401);
            result.put("msg", "未登录");
            result.put("data",null);
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write(JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue));
        }
        return false;
    }
}