package com.id0304.controller;

import com.id0304.common.controller.BaseController;
import com.id0304.entity.Consume;
import com.id0304.service.ConsumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Author WuZhengHua
 * @Description TODO 消费服务控制层
 * @Date 2019/9/5 22:53
 */
@RestController
@RequestMapping("/api/consume")
@Slf4j
public class ConsumeController extends BaseController{
    @Autowired
    ConsumeService consumeService;

    /**
     * @Author WuZhengHua
     * @Description TODO 生成订单
     * @Date 11:56 2019/9/15
     **/
    @PostMapping("/member/createConsume.action")
    public Map<String,Object> createConsume(@RequestBody Consume consume, HttpServletRequest request, HttpServletResponse response) {
        if (consume == null) {
            return setParamError();
        } else {
            String data = consumeService.addConsumeToken(consume, request, response);
            return setSuccess(data);
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 获取用户的订单信息
     * @Date 17:46 2019/9/16
     **/
    @GetMapping("/member/getMemberConsumes.action")
    public Map<String,Object> getMemberConsume(HttpServletRequest request) {
        if(request == null){
            return setParamError();
        }
        String data = consumeService.getMemberConsumes(request);;
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 判断用户选择的时间点是否可选
     * @Date 10:02 2019/9/17
     **/
    @GetMapping("/ifTimeCanSelect.action")
    public Map<String,Object> ifTimeCanSelect(@RequestParam("useTime") String useTime, @RequestParam("yachtId") String yachtId) {
        if (useTime == null||yachtId == null) {
            return setParamError();
        }
        String data = consumeService.ifTimeCanSelect(useTime, yachtId);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 管理员--获取所有订单列表(分页)
     * @Parament condition:消费号/会员编号/联系电话/联系人/游艇编号/订单号/消费金额 的模糊条件    state：状态（0：未支付 1：已支付 2：过期）productType: 产品类型(1:租赁 2:购买)      useTime:使用时间        created：订单创建时间      page：第几页    counts：每页个数         注：若为null则为未选择条件
     * @Date 21:16 2019/9/23
     **/
    @PostMapping("/admin/getConsumes.action")
    public Map<String,Object> getConsumes(String condition,String state,String productType, String minUseTime,String maxUseTime,String minCreated,String maxCreated, String page, String counts) {
        if(condition == null||state == null||productType == null||minUseTime == null||maxUseTime == null||minCreated == null||maxCreated == null||page == null|counts == null){
            return setParamError();
        }
        String data = consumeService.getConsumes(condition, state, productType, minUseTime, maxUseTime, minCreated, maxCreated, page, counts);
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 管理员--删除选中的无效订单,传入一个消费号数组
     * @Date 17:53 2019/9/26
     **/
    @PostMapping("/admin/deleteNullConsumes.action")
    public Map<String,Object> deleteNullConsumes(@RequestBody String[] batchDelList){
        if(batchDelList != null){
            String data = consumeService.deleteNullConsumes(batchDelList);
            return setSuccess(data);
        }else{
            return setParamError();
        }
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 删除所有无效订单
     * @Date 13:45 2019/10/4
     **/
    @GetMapping("/admin/deleteVoidOrders.action")
    public Map<String,Object> deleteVoidOrders(){
        String data = consumeService.deleteVoidOrders();
        return setSuccess(data);
    }

    /**
     * @Author WuZhengHua
     * @Description TODO 将所有过期未支付订单设置为无效订单
     * @Date 12:16 2019/10/6
     **/
    @GetMapping("/admin/setVoidOrders.action")
    public Map<String,Object> setVoidOrders(){
        String data = consumeService.setVoidOrders();
        return setSuccess(data);
    }
}
