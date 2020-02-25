package com.id0304.service;

import com.id0304.entity.Member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author WuZhengHua
 * @Description TODO 登陆注册服务层接口
 * @Date 16:14 2019/9/11
 **/
public interface UserService {

    String Md5AndBase64Solt(Long tel, String password);

    String doLogin(Member member, HttpServletResponse response);

    String doRegister(Member member);

    String logout(HttpServletRequest request, HttpServletResponse response);

    String getUserByToken(HttpServletRequest request);

    Member getUserByAdminAccount(Long tel);
}
