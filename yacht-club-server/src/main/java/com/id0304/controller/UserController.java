package com.id0304.controller;

import com.id0304.common.controller.BaseController;
import com.id0304.entity.Member;
import com.id0304.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @Author WuZhengHua
 * @Description TODO 用户登录注册控制层
 * @Date 2019/9/11 16:11
 */
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController extends BaseController{
    @Autowired
    private UserService userService;

    /**
     * @Author WuZhengHua
     * @Description TODO 用户登录服务     返回:1成功,2密码错误,3手机号不存在,4服务器错误
     * @Date 16:37 2019/9/11
     **/
    @PostMapping("/doLogin.action")
    public Map<String,Object> doLogin(@RequestBody Member member, HttpServletResponse response) {
        if (member == null) {
            return setParamError();
        }
        String data = userService.doLogin(member, response);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 管理员登录服务
     * @Date 11:32 2019/10/6
     **/
    @PostMapping("/adminLogin.action")
    public Map<String,Object> adminLogin(@RequestBody Member member , HttpServletResponse response){
        Member admin = userService.getUserByAdminAccount(member.getTel());
        if(admin == null){
            return setSuccess("信息错误");       //管理员账号不存在
        }else {
            return doLogin(member,response);
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 用户注册服务     返回:1成功,2手机号码格式错误,3密码包含空格错误,4手机号已存在,5服务器错误
     * @Date 16:37 2019/9/11
     **/
    @PostMapping("/doRegister.action")
    public Map<String,Object> doRegister(@RequestBody Member member) {
        if (member == null) {
            return setParamError();
        }
        String data = userService.doRegister(member);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 注销服务   返回:1注销成功 2注销失败
     * @Date 16:49 2019/9/12
     **/
    @GetMapping("/logout.action")
    public Map<String,Object> logout(HttpServletRequest request, HttpServletResponse response) {
        if(request == null||response == null){
            return setParamError();
        }
        String data = userService.logout(request, response);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 根据token获取用户信息
     * @Date 9:26 2019/9/13
     **/
    @GetMapping("/getUser.action")
    public Map<String,Object> getUserByToken(HttpServletRequest request) {
        if(request == null){
            return setParamError();
        }
        String data = userService.getUserByToken(request);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 判断管理员权限
     * @Date 18:41 2019/10/15
     **/
    @GetMapping("/admin/ifAdmin.action")
    public Map<String,Object> ifAdmin(){
        log.info("#####有管理员权限");
        return setSuccess();
    }
}
