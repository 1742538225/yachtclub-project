package com.id0304.common.config;

import com.id0304.common.filter.RestFilter;
import com.id0304.common.shiro.MyAuthenticationFilter;
import com.id0304.common.shiro.MyShiroRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.*;

/**
 * @Author WuZhengHua
 * @Description TODO shiro配置类
 * @Date 14:03 2019/7/15
 **/
@Configuration
public class ShiroConfig {
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //1.设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //2.shiro内置过滤器,可以实现拦截
        /**
         * anon:无需认证可以访问
         * authc:必须认证才能访问
         * user:如果使用rememberMe的功能,可以直接访问
         * perms:该资源必须得到资源权限才可以访问
         * role:该资源必须得到角色权限才能访问
         */

        /**自定义的过滤器*/
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("cors", new RestFilter()); //跨域
        filterMap.put("auth", new MyAuthenticationFilter());//认证过滤器
        shiroFilterFactoryBean.setFilters(filterMap);

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        filterChainDefinitionMap.put("/api/user/doLogin.action", "anon");       //执行登录放行
        filterChainDefinitionMap.put("/api/user/doRegister.action", "anon");    //执行注册放行
        filterChainDefinitionMap.put("/api/user/logout.action", "anon");        //执行注销放行

        filterChainDefinitionMap.put("/api/member/admin/**", "auth,perms[ROLE_ADMIN]");
        filterChainDefinitionMap.put("/api/consume/admin/**", "auth,perms[ROLE_ADMIN]");
        filterChainDefinitionMap.put("/api/yacht/admin/**", "auth,perms[ROLE_ADMIN]");
        filterChainDefinitionMap.put("/api/yachtImg/admin/**", "auth,perms[ROLE_ADMIN]");
        filterChainDefinitionMap.put("/api/user/admin/**", "auth,perms[ROLE_ADMIN]");
        filterChainDefinitionMap.put("/api/member/member/**", "auth,perms[ROLE_MEMBER]");
        filterChainDefinitionMap.put("/api/consume/member/**", "auth,perms[ROLE_MEMBER]");
        filterChainDefinitionMap.put("/api/pay/member/toPay.action", "auth,perms[ROLE_MEMBER]");

        filterChainDefinitionMap.put("/api/user/getUser.action", "auth");
        filterChainDefinitionMap.put("/api/consume/getMemberConsume.action", "auth");

        filterChainDefinitionMap.put("/api/file/**", "anon");
        filterChainDefinitionMap.put("/pay/callback/**", "anon");

        filterChainDefinitionMap.put("/**", "cors");       //注意优先级

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean(name = "myShiroRealm")
    public MyShiroRealm getMyShiroRealm() {
        return new MyShiroRealm();
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("myShiroRealm") MyShiroRealm myShiroRealm) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(myShiroRealm);
        return defaultWebSecurityManager;
    }
}