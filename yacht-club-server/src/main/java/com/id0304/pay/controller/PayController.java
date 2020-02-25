package com.id0304.pay.controller;

import com.alibaba.fastjson.JSONObject;
import com.id0304.common.contants.CookieConstants;
import com.id0304.common.utils.CookieUtil;
import com.id0304.common.utils.DateUtil;
import com.id0304.entity.Consume;
import com.id0304.pay.service.PayService;
import com.id0304.service.ConsumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/api/pay")
@Slf4j
public class PayController {
    @Autowired
    private ConsumeService consumeService;

    @Autowired
    private PayService payService;

    @GetMapping("/member/toPay.action")
    public void toPay(HttpServletRequest request,HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        //使用token查找对应的支付信息
        try {
            String payToken = CookieUtil.getUid(request, CookieConstants.COOKIE_NAME_PAYTOKEN);
            Consume consume = consumeService.getConsumeToken(payToken);
            if(consume == null){
                //没有找到该接口
                out.println("没有找到该订单,该订单已过期,请返回尝试重新下单");
                return;
            }
            Timestamp timestamp = consume.getUseTime();
            if(timestamp != null) {
                Date date = DateUtil.TimestampToDate(timestamp);
                String useTime = DateUtil.toStr(date);  //用户选择的时间点
                if (consumeService.ifTimeCanSelect(useTime, consume.getYachtScheme().getYachtId() + "") != null) {
                    consume.setState(2);
                    consume.setUpdated(DateUtil.getTimestamp());
                    consumeService.updateConsume(consume);
                    log.error("#####此时间点不可用,该订单已过期");
                    out.println("所选时间点已不可用,该订单过期");
                    return;
                }
            }
            if(consume.getYacht().getState().equals(3)){
                log.error("#####该游艇已售出");
                out.println("该游艇已售出");
                return;
            }
            log.info("#####成功跳转支付页面");
            String html = payService.pay(consume);
            out.println(html);
        } catch (Exception e) {
            e.printStackTrace();
            out.print("系统错误");
        }finally {
            out.close();
        }
    }
}
