package com.id0304.controller;

import com.id0304.common.controller.BaseController;
import com.id0304.common.utils.DateUtil;
import com.id0304.service.MemberService;
import com.id0304.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author WuZhengHua
 * @Description TODO 会员服务控制层
 * @Date 2019/9/5 22:52
 */
@RestController
@Slf4j
@RequestMapping("/api/member")
public class MemberController extends BaseController{
    @Autowired
    private MemberService memberService;

    /**
     * @Author WuZhengHua
     * @Description TODO 分页条件查询用户信息
     * @Date 20:12 2019/9/14
     **/
    @PostMapping("/admin/getMembers.action")
    public Map<String,Object> getMembers(String condition, String minAge, String maxAge, String sex, String level, String page, String counts){
        if(condition == null||minAge == null||maxAge == null||sex == null||level == null||page == null||counts == null){
            return setParamError();
        }
        String data = memberService.getMembers( condition,  minAge, maxAge, sex, level,  page,  counts);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 根据用户id获取用户信息,用于修改信息回显
     * @Date 13:34 2019/10/10
     **/
    @GetMapping("/admin/getMemberById.action")
    public Map<String,Object> getMemberById(String id){
        if(id == null){
            return setParamError();
        }
        String data = memberService.getMemberById(Long.parseLong(id));
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 插入用户
     * @Date 23:49 2019/9/5
     **/
    @PostMapping("/admin/addMember.action")
    public Map<String,Object> addMember(@RequestBody Member member) {
        if (member == null) {
            log.error("#####控制层-添加用户服务参数有误");
            return setParamError();
        }
        member.setCreated(DateUtil.getTimestamp());
        member.setUpdated(DateUtil.getTimestamp());
        log.info("#####成功插入用户信息,用户id:{}", member.getId());
        String data = memberService.addMember(member);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 修改用户基本信息(除手机号码和密码)
     * @Date 23:57 2019/9/5
     **/
    @PostMapping("/updateMemberMsg.action")
    public Map<String,Object> updateMemberMsg(@RequestBody Member member) {
        if (member == null) {
            log.error("#####控制层-修改用户信息参数有误");
            return setParamError();
        }
        log.info("#####成功修改用户信息,用户姓名:{}", member.getName());
        String data = memberService.updateMemberMsg(member);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 删除用户
     * @Date 23:59 2019/9/5
     **/
    @GetMapping("/admin/deleteMemberById.action")
    public Map<String,Object> deleteMemberById(@RequestParam("id") Long id) {
        if (id == null) {
            log.error("#####控制层-删除用户信息参数有误");
            return setParamError();
        }
        String data = memberService.deleteMemberById(id);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 修改密码
     * @Date 17:39 2019/10/6
     **/
    @PostMapping("/member/updatePassword.action")
    public Map<String,Object> updatePassword(String id,String oldPassword,String newPassword){
        if(id == null||oldPassword == null||newPassword == null){
            log.error("#####控制层-会员修改密码信息参数有误");
            return setParamError();
        }else{
            String data = memberService.updatePassword(Long.parseLong(id),oldPassword,newPassword);
            return setSuccess(data);
        }
    }
}
