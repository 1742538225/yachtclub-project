package com.id0304.service.impl;


import com.alibaba.fastjson.JSON;
import com.id0304.common.contants.TableName;
import com.id0304.common.utils.DateUtil;
import com.id0304.dao.ConsumeDao;
import com.id0304.dao.MemberDao;
import com.id0304.entity.Consume;
import com.id0304.entity.Member;
import com.id0304.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.id0304.service.MemberService;

import java.util.List;

/**
 * @Author WuZhengHua
 * @Description TODO 会员服务接口实现类
 * @Date 2019/9/5 22:51
 */
@Slf4j
@Service
public class MemberServiceImpl implements MemberService{
    @Autowired
    private MemberDao memberDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ConsumeDao consumeDao;

    /**
     * @Author WuZhengHua
     * @Description TODO 分页条件查询用户信息接口实现,返回list和总页数的json字符串格式{[],[],...,counts}
     * @Date 20:47 2019/9/14
     **/
    @Override
    public String getMembers(String condition, String minAge,String maxAge,String sex,String level, String page, String counts) {
        try {
            Member member = new Member();
            if (minAge.equals("")) {
                minAge = null;
            }
            if (maxAge.equals("")) {
                maxAge = null;
            }
            if (!sex.equals("")) {
                member .setSex(Integer.parseInt(sex));
            }
            if (!level.equals("")) {
                member .setLevel(Integer.parseInt(level));
            }
            if (!condition.equals("")) {
                condition = "%" + condition + "%";
            } else {
                condition = null;
            }
            Integer index = (Integer.parseInt(page) - 1) * Integer.parseInt(counts);    //分页查询第一条记录的索引

            List<List<?>> resultList = memberDao.getMembersByConAndPage(condition, minAge,maxAge,member, index, Integer.parseInt(counts));
            List<Member> consumeList = (List<Member>) resultList.get(0);      //结果集
            Long listCounts = (Long) resultList.get(1).get(0);   //分页查询数据总数
            String[] arr = new String[]{JSON.toJSONString(consumeList),listCounts+""};
            log.info("#####成功查询到用户列表");
            return JSON.toJSONString(arr);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####查询到用户列表失败,服务器异常");
            return null;
        }
    }

    @Override
    public String getMemberById(Long id) {
        try {
            Member member = memberDao.getMemberById(id);
            if(member == null){
                log.error("#####未找到id为{}的用户",id);
                return "未找到该用户";
            }
            log.info("#####找到id为{}的用户",id);
            return JSON.toJSONString(member);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####服务器错误");
            return "服务器错误";
        }
    }

    @Override
    public String addMember(Member member) {
        try {
            if(memberDao.getMemberByTel(member.getTel())!=null){    //手机号码或管理员账号已存在
                log.error("#####无法添加该用户,其手机号码或者管理员账号已存在");
                return "该账号或者手机号已存在";
            }else{
                member.setPassword(userService.Md5AndBase64Solt(member.getTel(),member.getPassword()));
                memberDao.save(member, TableName.TABLE_MEMBER);
                log.info("#####添加用户成功");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####添加用户失败,服务器异常");
            return "服务器错误";
        }
    }

    @Override
    public String updateMemberMsg(Member member) {
        try {
            member.setUpdated(DateUtil.getTimestamp());
            memberDao.updateMemberMsg(member);
            log.info("#####修改用户信息成功");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####服务器错误,修改用户信息失败");
            return "服务器错误";
        }
    }

    @Override
    public String deleteMemberById(Long id) {
        try {
            List<Consume> consumeList = consumeDao.getUseFulConsumeByMemberId(id);
            if(consumeList!=null&&consumeList.size()>0){
                log.error("#####无法删除该用户,该用户包含有效订单信息");
                return "无法删除该用户,该用户包含有效订单信息";
            }else{
                memberDao.deleteMemberById(id);
                consumeDao.deleteNullConsumeByMemberId(id);
                log.info("#####成功删除用户信息,用户id:{}", id);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####无法删除该用户,服务器错误");
            return "服务器错误";
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 修改密码
     * @Date 17:47 2019/10/6
     **/
    @Override
    public String updatePassword(Long id, String oldPassword, String newPassword) {
        try {
            if(newPassword.length()<6){     //判断新密码格式是否正确
                log.error("#####新密码格式错误,最短六位");
                return "输入的密码格式错误,最短六位";
            }else{
                Member member = memberDao.getMemberById(id);
                if(!member.getPassword().equals(userService.Md5AndBase64Solt(member.getTel(),oldPassword))){        //判断旧密码是否正确
                    log.error("#####旧密码验证错误");
                    return "原密码错误";
                }else{
                    memberDao.updatePwdById(id,userService.Md5AndBase64Solt(member.getTel(),newPassword));
                    log.info("#####修改密码成功");
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("#####服务器错误");
            return "服务器错误";
        }
    }
}
