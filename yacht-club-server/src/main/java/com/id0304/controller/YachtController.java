package com.id0304.controller;

import com.id0304.common.controller.BaseController;
import com.id0304.entity.Yacht;
import com.id0304.service.YachtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author WuZhengHua
 * @Description TODO 游艇服务控制层
 * @Date 2019/9/5 22:55
 */
@RestController
@RequestMapping("/api/yacht")
public class YachtController extends BaseController{
    @Autowired
    private YachtService yachtService;

    /**
     * @Author WuZhengHua
     * @Description TODO 条件分页获取游艇信息控制层
     * condition    模糊条件
     * minLength    最小游艇长度
     * maxLength    最大游艇长度
     * minMembers   最小限载人数
     * maxMembers   最大限载人数
     * state        状态
     * maintenance  维护周期
     * page         选择页数
     * counts       一页显示条数
     * @Date 13:27 2019/10/3
     **/
    @PostMapping("/admin/getYachts.action")
    public Map<String,Object> getYachts(String condition, String minLength, String maxLength, String minMembers, String maxMembers, String state, String maintenance, String page, String counts) {
        if(condition == null||minLength == null||maxLength == null||minMembers == null||maxMembers == null||state == null||maintenance == null||page == null||counts == null){
            return setParamError();
        }
        String data = yachtService.getYachts(condition, minLength, maxLength, minMembers, maxMembers, state, maintenance, page, counts);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 添加游艇控制层,完成添加游艇和游艇套餐
     * @Date 20:10 2019/9/29
     **/
    @PostMapping("/admin/addYacht.action")
    public Map<String,Object> addYacht(@RequestBody Yacht yacht) {
        if (yacht == null) {
            return setParamError();
        }
        String data = yachtService.addYacht(yacht);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 修改游艇控制层,完成添加游艇和游艇套餐,返回值: 游艇id:成功 -1参数错误 -2服务器错误
     * @Date 16:16 2019/10/2
     **/
    @PostMapping("/admin/updateYacht.action")
    public Map<String,Object> updateYacht(@RequestBody Yacht yacht) {
        if (yacht == null) {
            return setParamError();
        }
        String data = yachtService.updateYacht(yacht);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 根据游艇id删除游艇,并进行判断是否可以删除     返回值: 1成功    2客户端传入参数错误  3游艇包含有效订单   4服务器异常
     * @Date 16:01 2019/10/3
     **/
    @GetMapping("/admin/deleteYachtById.action")
    public Map<String,Object> deleteYachtById(@RequestParam("yachtId") String yachtId){
        if(yachtId == null){
            return setParamError();
        }else{
            String data = yachtService.deleteYachtById(yachtId);
            return setSuccess(data);
        }
    }

    @GetMapping("/getYachtMessageById.action")
    public Map<String,Object> getYachtMessageById(@RequestParam("id") String id) {
        if(id == null){
            return setParamError();
        }else{
            String data = yachtService.getYachtMessage(Long.parseLong(id));
            return setSuccess(data);
        }
    }

    @GetMapping("/getYachtsAndMainPicByHire.action")
    public Map<String,Object> getYachtsAndMainPicByHire() {
        String data = yachtService.getYachtsAndMainPicByState(1);  //查询所有正在出租的游艇
        return setSuccess(data);
    }

    @GetMapping("/getYachtsAndMainPicBySale.action")
    public Map<String,Object> getYachtsAndMainPicBySale() {
        String data = yachtService.getYachtsAndMainPicByState(2);//查询所有正在销售的游艇
        return setSuccess(data);
    }
}
