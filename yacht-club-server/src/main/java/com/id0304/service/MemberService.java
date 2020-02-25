package com.id0304.service;

import com.id0304.entity.Member;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author WuZhengHua
 * @Description TODO 用户信息服务层接口
 * @Date 23:03 2019/9/5
 **/
public interface MemberService {
    /**
     * @Author WuZhengHua
     * @Description TODO service接口--分页条件查询用户信息
     * @Date 20:47 2019/9/14
     **/
    String getMembers(String condition, String minAge,String maxAge,String sex,String level, String page, String counts);

    /**
     * @Author WuZhengHua
     * @Description TODO service接口--根据用户id获取用户信息,用于修改信息回显
     * @Date 20:47 2019/9/14
     **/
    String getMemberById(Long id);

    /**
     * @Author WuZhengHua
     * @Description TODO service接口--插入用户
     * @Date 23:52 2019/9/5
     **/
    String addMember(Member member);

    /**
     * @Author WuZhengHua
     * @Description TODO service接口--修改用户基本信息
     * @Date 23:53 2019/9/5
     **/
    String updateMemberMsg(Member member);

    /**
     * @Author WuZhengHua
     * @Description TODO service接口--删除用户
     * @Date 23:54 2019/9/5
     **/
    String deleteMemberById(Long id);

    /**
     * @Author WuZhengHua
     * @Description TODO service接口--会员修改账号密码
     * @Date 23:54 2019/9/5
     **/
    String updatePassword(Long id, String oldPassword, String newPassword);

}
