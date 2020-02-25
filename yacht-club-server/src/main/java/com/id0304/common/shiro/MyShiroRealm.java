package com.id0304.common.shiro;

import com.id0304.dao.MemberDao;
import com.id0304.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author WuZhengHua
 * @Description TODO 自定义Realm程序,实现认证和授权逻辑
 * @Date 2019/7/14 12:04
 */
@Slf4j
public class MyShiroRealm extends AuthorizingRealm {
    @Autowired
    private MemberDao memberDao;

    /**
     * @Author WuZhengHua
     * @Description TODO 授权逻辑
     * @Date 12:19 2019/7/14
     **/
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("#####执行授权逻辑");
        Subject subject = SecurityUtils.getSubject();
        //获取认证完的对象,在认证逻辑通过SimpleAuthenticationInfo第一个参数传入
        Member member = (Member) subject.getPrincipal();
        Integer level = member.getLevel();   //0为普通会员 1为管理员     2为超管
        String role = null;
        if(level.equals(0)){
            role = "ROLE_MEMBER";
        }else if(level.equals(1)||level.equals(2)){
            role = "ROLE_ADMIN";
        }
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        //根据数据库查值授权
        simpleAuthorizationInfo.addStringPermission(role);
        return simpleAuthorizationInfo;
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 认证逻辑
     * @Date 12:19 2019/7/14
     **/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("#####执行认证逻辑");
        //1.判断用户名密码
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        String tel = usernamePasswordToken.getUsername();
        //2.根据usernamePasswordToken中用户登录存进的用户名去数据库查询
        Member member = memberDao.getMemberByTel(Long.parseLong(tel));
        if (member == null) {     //未找到该用户
            return null;        //shiro底层会抛出UnknownAccountException
        }
        //第一个参数,传入授权逻辑要获取的对象
        //第二个参数,判断数据库查到的密码和用户输入的密码是否一致,不一致会抛出IncorrectCredentialsException异常供捕获
        //第三个参数,是realm的名称,这里可以不指定
        return new SimpleAuthenticationInfo(member, member.getPassword(), "");
    }
}
