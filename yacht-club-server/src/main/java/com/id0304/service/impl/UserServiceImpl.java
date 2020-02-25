package com.id0304.service.impl;

import com.alibaba.fastjson.JSON;
import com.id0304.common.contants.CookieConstants;
import com.id0304.common.contants.RedisContants;
import com.id0304.common.contants.TableName;
import com.id0304.common.utils.*;
import com.id0304.dao.MemberDao;
import com.id0304.entity.Member;
import com.id0304.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author WuZhengHua
 * @Description TODO 登陆注册服务接口实现类
 * @Date 2019/9/11 16:14
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private MemberDao memberDao;

    @Autowired
    private BaseRedisService baseRedisService;

    /**
     * @Author WuZhengHua
     * @Description TODO 会员登录服务
     * @Date 16:36 2019/9/11
     **/
    @Override
    public String doLogin(Member member, HttpServletResponse response) {
        Long tel = member.getTel();
        String password = member.getPassword();
        String newPassword = Md5AndBase64Solt(tel, password);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(tel + "", newPassword);
        try {
            subject.login(usernamePasswordToken);
            log.info("#####登陆成功,用户帐号:{}", tel);
            String token = TokenUtil.getUserToken();
            //存入redis token-tel 键值对
            baseRedisService.setString(token, member.getTel() + "", RedisContants.REDIS_TIMEOUT_USER_7DAY);
            CookieUtil.addCookie(response, CookieConstants.COOKIE_NAME_USERTOKEN, token, CookieConstants.COOKIE_TIMEOUT_7DAY);
            return null;   //登陆成功
        } catch (IncorrectCredentialsException e) {
            log.error("#####登陆失败,密码错误,用户帐号:{}", tel);
            return "密码错误";   //密码错误
        } catch (AuthenticationException e) {
            log.error("#####登陆失败,用户帐号:{} 不存在", tel);
            return "帐号不存在";   //手机号错误
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####服务器错误");
            return "服务器错误";   //手机号错误
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 用户注册服务
     * @Date 16:44 2019/9/11
     **/
    @Override
    public String doRegister(Member member) {
        try {
            Long tel = member.getTel();
            String password = member.getPassword();
            Member memberByTel = memberDao.getMemberByTel(tel);
            if(memberByTel != null){
                log.error("#####注册服务-手机号已存在");
                return "手机号已存在";
            }
            if (!isMobileNO(tel + "")) {
                log.error("#####注册服务-手机号码格式有误");
                return "手机号码格式错误";   //手机号码格式有误
            }
            if (password.length()<6) {
                log.error("#####注册服务-密码长度最短六位");
                return "密码长度最短六位";   //密码长度最短六位
            }
            String newPassword = Md5AndBase64Solt(tel, password);
            member.setPassword(newPassword);
            member.setLevel(0); //默认注册为会员
            member.setCreated(DateUtil.getTimestamp());
            member.setUpdated(DateUtil.getTimestamp());
            memberDao.save(member, TableName.TABLE_MEMBER);
            log.info("#####注册成功!用户手机号码:{}", member.getTel());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####注册服务-服务器错误");
            return "服务器错误";
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 注销服务
     * @Date 16:52 2019/9/12
     **/
    @Override
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();
            String userToken = CookieUtil.getUid(request, CookieConstants.COOKIE_NAME_USERTOKEN);
            baseRedisService.delete(userToken);
            CookieUtil.removeCookie(response, CookieConstants.COOKIE_NAME_USERTOKEN);
            log.info("#####注销成功!");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####注销失败!服务器错误");
            return "服务器错误";
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 获取用户信息服务
     * @Date 9:55 2019/9/13
     **/
    @Override
    public String getUserByToken(HttpServletRequest request) {
        try {
            String token = CookieUtil.getUid(request, CookieConstants.COOKIE_NAME_USERTOKEN);
            if (token == null) {
                log.error("#####用户身份过期,未在cookie中找到token");
                return "用户身份过期";
            }
            String tel = baseRedisService.get(token);
            if (tel == null) {
                log.error("#####用户身份过期,未在redis中找到tel");
                return "用户身份过期";
            }
            Member user = memberDao.getMemberByTel(Long.parseLong(tel));
            user.setPassword(null);
            return JSON.toJSONString(user);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.error("#####服务器异常,获取用户信息失败");
            return "服务器错误";
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 根据管理员账号查询管理员
     * @Date 11:37 2019/10/6
     **/
    @Override
    public Member getUserByAdminAccount(Long account) {
        return memberDao.getAdminByAccount(account);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 判断是否为手机号码
     * @Date 16:49 2019/9/11
     **/
    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 用户密码加盐加密
     * @Date 16:22 2019/9/11
     **/
    @Override
    public String Md5AndBase64Solt(Long tel, String password) {
        String solt = tel + password;
        String ps_md5 = Md5Util.getMD5(solt);
        return Base64Util.encode(ps_md5);
    }
}
